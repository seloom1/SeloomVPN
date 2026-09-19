package com.example.vpn

import android.net.Uri
import com.example.model.VpnServer
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object WireGuardLinkParser {

    /**
     * Parses a wireguard:// URI or a raw .conf string into a VpnServer.
     */
    fun parse(input: String): VpnServer? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        return if (trimmed.startsWith("wireguard://", ignoreCase = true)) {
            parseUri(trimmed)
        } else if (trimmed.contains("[Interface]", ignoreCase = true)) {
            parseConf(trimmed)
        } else {
            // Try parsing as URI anyway
            parseUri(trimmed)
        }
    }

    private fun parseUri(rawUriString: String): VpnServer? {
        try {
            // Clean up accidental line breaks or spaces often introduced when copying through chat apps
            val sanitized = sanitizeUriString(rawUriString)
            val uri = Uri.parse(sanitized)

            var fragmentName = uri.fragment?.let { decode(it).trim() }
            if (fragmentName.isNullOrBlank()) {
                val hashIdx = sanitized.lastIndexOf('#')
                if (hashIdx != -1) {
                    fragmentName = decode(sanitized.substring(hashIdx + 1)).trim()
                }
            }
            if (fragmentName.isNullOrBlank()) {
                fragmentName = "WireGuard Server"
            }

            // Extract endpoint: host + port
            val host = uri.host ?: "engage.cloudflareclient.com"
            val port = if (uri.port != -1) uri.port else 2408
            val endpoint = "$host:$port"

            // Private key can be in user info or query param
            var privateKey = uri.getQueryParameter("privatekey")
                ?: uri.getQueryParameter("private_key")
                ?: uri.getQueryParameter("pk")

            if (privateKey == null && uri.userInfo != null) {
                privateKey = decode(uri.userInfo!!)
            }

            // Public key
            val publicKey = uri.getQueryParameter("publickey")
                ?: uri.getQueryParameter("public_key")
                ?: uri.getQueryParameter("peer_public_key")
                ?: ""

            // Address
            val address = uri.getQueryParameter("address")
                ?: uri.getQueryParameter("ip")
                ?: "172.16.0.2/32, 2606:4700:110:8d70:8df1:6e3d:693b:ea40/128"

            // DNS
            val dns = uri.getQueryParameter("dns") ?: "1.1.1.1, 1.0.0.1"

            // MTU
            val mtuStr = uri.getQueryParameter("mtu")
            val mtu = mtuStr?.toIntOrNull() ?: 1280

            // AllowedIPs
            val allowedIps = uri.getQueryParameter("allowedips")
                ?: uri.getQueryParameter("allowed_ips")
                ?: "0.0.0.0/0, ::/0"

            val cleanPrivateKey = cleanKey(privateKey ?: "")
            val cleanPublicKey = cleanKey(publicKey)

            if (cleanPublicKey.isEmpty() && cleanPrivateKey.isEmpty()) {
                return null
            }

            return VpnServer(
                name = fragmentName,
                endpoint = endpoint,
                privateKey = cleanPrivateKey,
                publicKey = cleanPublicKey,
                address = decode(address),
                dns = decode(dns),
                mtu = mtu,
                allowedIps = decode(allowedIps),
                rawLink = rawUriString,
                isCustom = true,
                note = "WireGuard Import"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun parseConf(confText: String): VpnServer? {
        try {
            var privateKey = ""
            var address = "172.16.0.2/32"
            var dns = "1.1.1.1"
            var mtu = 1280
            var publicKey = ""
            var endpoint = "engage.cloudflareclient.com:2408"
            var allowedIps = "0.0.0.0/0, ::/0"
            var name = "WireGuard Config"

            val lines = confText.lines()
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("#")) {
                    val comment = trimmed.removePrefix("#").trim()
                    if (comment.isNotEmpty() && name == "WireGuard Config") {
                        name = comment
                    }
                    continue
                }
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim().lowercase()
                    val value = parts[1].trim()
                    when (key) {
                        "privatekey" -> privateKey = value
                        "address" -> address = value
                        "dns" -> dns = value
                        "mtu" -> mtu = value.toIntOrNull() ?: 1280
                        "publickey" -> publicKey = value
                        "endpoint" -> endpoint = value
                        "allowedips" -> allowedIps = value
                    }
                }
            }

            if (privateKey.isEmpty() && publicKey.isEmpty()) return null

            return VpnServer(
                name = name,
                endpoint = endpoint,
                privateKey = privateKey,
                publicKey = publicKey,
                address = address,
                dns = dns,
                mtu = mtu,
                allowedIps = allowedIps,
                rawLink = confText,
                isCustom = true,
                note = "Conf File Import"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun sanitizeUriString(input: String): String {
        var s = input.replace("\r", "").replace("\n", "").trim()
        val parts = s.split("#", limit = 2)
        var base = parts[0]
        val fragment = if (parts.size > 1) parts[1] else null

        // Remove spaces inside %-encoded octets like "% 2F", "%2 F", "% 2 F", "% 2C"
        base = base.replace(Regex("%\\s*([0-9a-fA-F])\\s*([0-9a-fA-F])"), "%$1$2")
        // Fix spaces around domain e.g. "co m:2408" -> "com:2408"
        base = base.replace(Regex("\\.co\\s+m"), ".com")
        base = base.replace(Regex("cloudflareclient\\s*\\.\\s*com"), "cloudflareclient.com")
        // Remove spaces around ? and & and = and @
        base = base.replace("? ", "?").replace(" ?", "?")
            .replace("& ", "&").replace(" &", "&")
            .replace("= ", "=").replace(" =", "=")
            .replace("@ ", "@").replace(" @", "@")

        return if (fragment != null) "$base#$fragment" else base
    }

    private fun cleanKey(key: String): String {
        val decoded = if (key.contains("%")) Uri.decode(key) else key
        return decoded.replace(" ", "").trim()
    }

    private fun decode(value: String): String {
        return try {
            Uri.decode(value) ?: value
        } catch (e: Exception) {
            value
        }
    }

    val defaultServers: List<VpnServer> = listOf(
        VpnServer(
            id = "seloom1_warp_bally",
            name = "Seloom VPN BALLY",
            endpoint = "engage.cloudflareclient.com:2408",
            privateKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            publicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
            address = "172.16.0.2/32, 2606:4700:110:8d70:8df1:6e3d:693b:ea40/128",
            dns = "1.1.1.1, 1.0.0.1, 2606:4700:4700::1111, 2606:4700:4700::1001",
            mtu = 1280,
            allowedIps = "0.0.0.0/0, ::/0",
            rawLink = "wireguard://AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D@engage.cloudflareclient.com:2408?address=172.16.0.2%2F32%2C2606%3A4700%3A110%3A8d70%3A8df1%3A6e3d%3A693b%3Aea40%2F128&publickey=bmXOC%2BF1FxEMF9dyiK2H5%2F1SUtzH0JuVo51h2wPfgyo%3D&privatekey=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D#Seloom VPN%20BALLY",
            note = "يعمل على خط OODI بعد اختيار تطبيق بلي"
        ),
        VpnServer(
            id = "seloom1_warp_fast",
            name = "Seloom VPN FAST 01",
            endpoint = "162.159.192.1:2408",
            privateKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            publicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
            address = "172.16.0.2/32, 2606:4700:110:8d70:8df1:6e3d:693b:ea40/128",
            dns = "1.1.1.1, 1.0.0.1",
            mtu = 1280,
            allowedIps = "0.0.0.0/0, ::/0",
            note = "سيرفر اتصال سريع - Cloudflare"
        ),
        VpnServer(
            id = "seloom1_warp_iraq",
            name = "Seloom VPN IRAQ VIP",
            endpoint = "162.159.193.5:2408",
            privateKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            publicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
            address = "172.16.0.2/32, 2606:4700:110:8d70:8df1:6e3d:693b:ea40/128",
            dns = "1.1.1.1, 1.0.0.1",
            mtu = 1280,
            allowedIps = "0.0.0.0/0, ::/0",
            note = "سيرفر فائق السرعة مخصص للعراق"
        )
    )
}
