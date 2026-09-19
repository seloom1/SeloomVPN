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
            Request.Builder().url("https://ipapi.co/json/").header("User-Agent", "SeloomVPN/1.0").build()
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
                    if (ip.isNotBlank()) {
                        return@withContext NetworkInfo(
                            ip = ip,
                            country = country.ifBlank { "غير معروف" },
                            countryCode = code,
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
