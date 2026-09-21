package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.VpnServer
import com.example.vpn.WireGuardLinkParser
import org.json.JSONArray
import org.json.JSONObject

class ServerRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("seloom_vpn_prefs", Context.MODE_PRIVATE)

    fun getSavedServers(): List<VpnServer> {
        val jsonStr = prefs.getString("custom_servers", null)
        val customServers = mutableListOf<VpnServer>()

        if (!jsonStr.isNullOrEmpty()) {
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    customServers.add(
                        VpnServer(
                            id = obj.optString("id"),
                            name = obj.optString("name"),
                            endpoint = obj.optString("endpoint"),
                            privateKey = obj.optString("privateKey"),
                            publicKey = obj.optString("publicKey"),
                            address = obj.optString("address"),
                            dns = obj.optString("dns"),
                            mtu = obj.optInt("mtu", 1280),
                            allowedIps = obj.optString("allowedIps", "0.0.0.0/0, ::/0"),
                            rawLink = obj.optString("rawLink"),
                            isCustom = true,
                            note = obj.optString("note")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return WireGuardLinkParser.defaultServers + customServers
    }

    fun addCustomServer(server: VpnServer) {
        val current = getSavedServers().filter { it.isCustom }.toMutableList()
        current.add(0, server)
        saveCustomServers(current)
    }

    fun deleteCustomServer(serverId: String) {
        val current = getSavedServers().filter { it.isCustom && it.id != serverId }
        saveCustomServers(current)
    }

    private fun saveCustomServers(servers: List<VpnServer>) {
        val array = JSONArray()
        for (s in servers) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("endpoint", s.endpoint)
                put("privateKey", s.privateKey)
                put("publicKey", s.publicKey)
                put("address", s.address)
                put("dns", s.dns)
                put("mtu", s.mtu)
                put("allowedIps", s.allowedIps)
                put("rawLink", s.rawLink)
                put("note", s.note)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_servers", array.toString()).apply()
    }

    fun getSelectedServerId(): String? {
        return prefs.getString("selected_server_id", null)
    }

    fun setSelectedServerId(id: String) {
        prefs.edit().putString("selected_server_id", id).apply()
    }

    fun getExcludedApplications(): Set<String> =
        prefs.getStringSet("excluded_applications", emptySet()).orEmpty()

    fun setExcludedApplications(packages: Set<String>) {
        prefs.edit().putStringSet("excluded_applications", packages).apply()
    }
}
