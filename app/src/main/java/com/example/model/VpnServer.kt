package com.example.model

import java.util.UUID

data class VpnServer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val endpoint: String,
    val privateKey: String,
    val publicKey: String,
    val address: String = "172.16.0.2/32, 2606:4700:110:8d70:8df1:6e3d:693b:ea40/128",
    val dns: String = "1.1.1.1, 1.0.0.1",
    val mtu: Int = 1280,
    val allowedIps: String = "0.0.0.0/0, ::/0",
    val rawLink: String = "",
    val isCustom: Boolean = false,
    val note: String = ""
) {
    fun toWireGuardConfigText(excludedApplications: Set<String> = emptySet()): String {
        val excluded = excludedApplications.filter { it.isNotBlank() }.joinToString(",")
        val excludedLine = if (excluded.isNotBlank()) "ExcludedApplications = $excluded\n" else ""
        return """
[Interface]
PrivateKey = $privateKey
Address = $address
DNS = $dns
        MTU = $mtu
$excludedLine
[Peer]
PublicKey = $publicKey
Endpoint = $endpoint
AllowedIPs = $allowedIps
PersistentKeepalive = 25
        """.trimIndent()
    }
}
