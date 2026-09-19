package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vpn.WireGuardLinkParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Seloom VPN", appName)
    }

    @Test
    fun `parse user wire link correctly`() {
        val link = "wireguard://AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D@engage.cloudflareclient.co m:2408? address=172.16.0.2% 2F32% 2C2606%3A4700%3A11 0%3A8d70%3A8df1%3A6e3d%3A693b%3Aea40% 2F128&publickey=bmXOC%2BF1FxEMF9dyiK2H5%2 F1SUtzH0JuVo51h2wPfgyo%3D&privatekey=98qlf4c Snq2VMmonjeWZI1dS1994IzfbR%2FfRdG%2FiCoE% 3D#SELOOM1-WARP%20BALLY%20"
        val server = WireGuardLinkParser.parse(link)
        assertNotNull(server)
        assertEquals("SELOOM1-WARP BALLY", server!!.name)
        assertEquals("engage.cloudflareclient.com:2408", server.endpoint)
        assertEquals("bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=", server.publicKey)
        assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", server.privateKey)

        val conf = server.toWireGuardConfigText()
        assertTrue(conf.contains("[Interface]"))
        assertTrue(conf.contains("[Peer]"))
        assertTrue(conf.contains("Endpoint = engage.cloudflareclient.com:2408"))
    }
}
