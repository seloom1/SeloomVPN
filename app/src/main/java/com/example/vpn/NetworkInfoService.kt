package com.example.vpn

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

data class NetworkInfo(
    val ip: String,
    val country: String,
    val countryCode: String,
    val pingMs: Int
)

class NetworkInfoService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun fetchNetworkInfo(): NetworkInfo = withContext(Dispatchers.IO) {
        val ping = measurePing()
        val providers = listOf(
            Request.Builder().url("https://ipwho.is/?lang=ar").header("User-Agent", "SeloomVPN/1.0").build(),
            Request.Builder().url("https://ipapi.co/json/").header("User-Agent", "SeloomVPN/1.0").build(),
            // api4 explicitly resolves over IPv4 when the network also has IPv6.
            Request.Builder().url("https://api4.ipify.org?format=json").header("User-Agent", "SeloomVPN/1.0").build()
        )
        for (request in providers) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val body = response.body?.string().orEmpty()
                    if (body.isBlank()) return@use
                    val json = JSONObject(body)
                    if (json.has("success") && !json.optBoolean("success")) return@use
                    val ip = json.optString("ip").trim()
                    val code = json.optString("country_code").ifBlank {
                        json.optString("countryCode")
                    }.uppercase()
                    val country = json.optString("country").ifBlank {
                        json.optString("country_name")
                    }.trim()
                    if (isIpv4(ip)) {
                        val location = if (country.isBlank() || code.isBlank()) {
                            lookupLocation(ip)
                        } else {
                            null
                        }
                        return@withContext NetworkInfo(
                            ip = ip,
                            country = country.ifBlank { location?.first ?: "غير معروف" },
                            countryCode = code.ifBlank { location?.second.orEmpty() },
                            pingMs = ping
                        )
                    }
                }
            } catch (_: Exception) {
                // Try the next independent provider.
            }
        }
        NetworkInfo("—", "غير متاح", "", ping)
    }

    private fun lookupLocation(ip: String): Pair<String, String>? {
        val requests = listOf(
            Request.Builder().url("https://ipwho.is/$ip?lang=ar").header("User-Agent", "SeloomVPN/1.0").build(),
            Request.Builder().url("https://ipapi.co/$ip/json/").header("User-Agent", "SeloomVPN/1.0").build()
        )
        for (request in requests) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val json = JSONObject(response.body?.string().orEmpty())
                    if (json.has("success") && !json.optBoolean("success")) return@use
                    val country = json.optString("country").ifBlank {
                        json.optString("country_name")
                    }.trim()
                    val code = json.optString("country_code").ifBlank {
                        json.optString("countryCode")
                    }.uppercase()
                    if (country.isNotBlank() || code.isNotBlank()) {
                        return Pair(country, code)
                    }
                }
            } catch (_: Exception) {
                // Try the next location provider.
            }
        }
        return null
    }

    private fun isIpv4(value: String): Boolean {
        val parts = value.split('.')
        return parts.size == 4 && parts.all { part ->
            part.isNotEmpty() && part.length <= 3 && part.all(Char::isDigit) &&
                part.toIntOrNull()?.let { it in 0..255 } == true
        }
    }

    suspend fun measurePing(): Int = withContext(Dispatchers.IO) {
        val targets = listOf("1.1.1.1" to 53, "8.8.8.8" to 53)
        for ((host, port) in targets) {
            try {
                val start = System.currentTimeMillis()
                Socket().use { it.connect(InetSocketAddress(host, port), 1500) }
                return@withContext (System.currentTimeMillis() - start).toInt()
            } catch (_: Exception) {
                // Continue to the next target.
            }
        }
        0
    }
}
