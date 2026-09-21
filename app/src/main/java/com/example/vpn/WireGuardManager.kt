package com.example.vpn

import android.content.Context
import android.util.Log
import com.example.model.VpnServer
import com.example.data.ServerRepository
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Statistics
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

enum class VpnStatus { DISCONNECTED, CONNECTING, CONNECTED, ERROR }

data class SpeedMetrics(
    val downloadSpeedKBps: Float = 0f,
    val uploadSpeedKBps: Float = 0f,
    val pingMs: Int = 0,
    val ipAddress: String = "—",
    val country: String = "غير معروف",
    val countryCode: String = "",
    val totalDownloadBytes: Long = 0L,
    val totalUploadBytes: Long = 0L,
    val durationSeconds: Long = 0L
)

class WireGuardManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val networkInfoService = NetworkInfoService()
    private val repository = ServerRepository(context.applicationContext)
    private val backend: GoBackend? = try {
        GoBackend(context.applicationContext)
    } catch (e: Exception) {
        Log.e(TAG, "Unable to initialise WireGuard backend", e)
        null
    }

    private var activeTunnel: Tunnel? = null
    private val _vpnStatus = MutableStateFlow(VpnStatus.DISCONNECTED)
    val vpnStatus: StateFlow<VpnStatus> = _vpnStatus.asStateFlow()
    private val _metrics = MutableStateFlow(SpeedMetrics())
    val metrics: StateFlow<SpeedMetrics> = _metrics.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var statsJob: Job? = null
    private var connectionStartTime = 0L
    private var lastRxBytes = 0L
    private var lastTxBytes = 0L

    fun connect(server: VpnServer) {
        scope.launch { connectNow(server) }
    }

    /** Replaces the active tunnel only after the previous tunnel is fully down. */
    fun reconnect(server: VpnServer) {
        scope.launch {
            disconnectNow()
            delay(300)
            connectNow(server)
        }
    }

    private suspend fun connectNow(server: VpnServer) {
        try {
            _vpnStatus.value = VpnStatus.CONNECTING
            _errorMessage.value = null
            val currentBackend = backend ?: error("لم يتم تهيئة محرك WireGuard")
            require(server.privateKey.isNotBlank()) { "مفتاح WireGuard الخاص مفقود" }
            require(server.publicKey.isNotBlank()) { "مفتاح WireGuard العام مفقود" }
            require(server.endpoint.contains(":")) { "عنوان السيرفر غير صالح" }
            val excludedApplications = repository.getExcludedApplications()
                .filter { it.isNotBlank() && it != context.packageName }
                .toSet()
            Log.i(TAG, "Applying ${excludedApplications.size} excluded applications")
            val config = Config.parse(
                ByteArrayInputStream(server.toWireGuardConfigText(excludedApplications).toByteArray(StandardCharsets.UTF_8))
            )
            val tunnel = object : Tunnel {
                override fun getName(): String = TUNNEL_NAME
                override fun onStateChange(newState: Tunnel.State) {
                    Log.d(TAG, "Tunnel state changed: $newState")
                    when (newState) {
                        Tunnel.State.UP -> {
                            _vpnStatus.value = VpnStatus.CONNECTED
                            onConnected()
                        }
                        Tunnel.State.DOWN -> {
                            _vpnStatus.value = VpnStatus.DISCONNECTED
                            onDisconnected()
                        }
                        Tunnel.State.TOGGLE -> Unit
                    }
                }
            }
            activeTunnel = tunnel
            currentBackend.setState(tunnel, Tunnel.State.UP, config)
        } catch (e: Exception) {
            Log.e(TAG, "Connection error", e)
            _vpnStatus.value = VpnStatus.ERROR
            _errorMessage.value = e.localizedMessage ?: "فشل تشغيل نفق WireGuard"
            stopMonitoring()
        }
    }

    fun disconnect() {
        scope.launch { disconnectNow() }
    }

    private suspend fun disconnectNow() {
        try {
            activeTunnel?.let { tunnel -> backend?.setState(tunnel, Tunnel.State.DOWN, null) }
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting", e)
        } finally {
            _vpnStatus.value = VpnStatus.DISCONNECTED
            onDisconnected()
        }
    }

    private fun onConnected() {
        connectionStartTime = System.currentTimeMillis()
        lastRxBytes = 0L
        lastTxBytes = 0L
        scope.launch { refreshNetworkInfo() }
        startMonitoring()
    }

    private fun onDisconnected() {
        stopMonitoring()
        _metrics.value = _metrics.value.copy(
            downloadSpeedKBps = 0f,
            uploadSpeedKBps = 0f,
            durationSeconds = 0L,
            totalDownloadBytes = 0L,
            totalUploadBytes = 0L
        )
    }

    private fun startMonitoring() {
        statsJob?.cancel()
        statsJob = scope.launch {
            var seconds = 0
            while (isActive && _vpnStatus.value == VpnStatus.CONNECTED) {
                delay(1000)
                seconds++
                var currentRx = 0L
                var currentTx = 0L
                try {
                    activeTunnel?.let { tunnel ->
                        backend?.getStatistics(tunnel)?.let { stats: Statistics ->
                            currentRx = stats.totalRx()
                            currentTx = stats.totalTx()
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Unable to read WireGuard statistics", e)
                }

                val rxDiff = if (currentRx >= lastRxBytes) currentRx - lastRxBytes else 0L
                val txDiff = if (currentTx >= lastTxBytes) currentTx - lastTxBytes else 0L
                // KB/s = bytes / 1024 / second. The UI promotes values >= 1024 KB/s to MB/s.
                val dlRate = (rxDiff / 1024.0).toFloat()
                val ulRate = (txDiff / 1024.0).toFloat()
                lastRxBytes = currentRx
                lastTxBytes = currentTx

                if (seconds % 5 == 0) {
                    _metrics.value = _metrics.value.copy(pingMs = networkInfoService.measurePing())
                }
                if (seconds % 30 == 0) refreshNetworkInfo()
                _metrics.value = _metrics.value.copy(
                    downloadSpeedKBps = dlRate,
                    uploadSpeedKBps = ulRate,
                    totalDownloadBytes = currentRx,
                    totalUploadBytes = currentTx,
                    durationSeconds = (System.currentTimeMillis() - connectionStartTime) / 1000
                )
            }
        }
    }

    private fun stopMonitoring() {
        statsJob?.cancel()
        statsJob = null
    }

    private suspend fun refreshNetworkInfo() {
        val info = networkInfoService.fetchNetworkInfo()
        _metrics.value = _metrics.value.copy(
            ipAddress = info.ip,
            country = info.country,
            countryCode = info.countryCode,
            pingMs = info.pingMs
        )
    }

    companion object {
        private const val TAG = "WireGuardManager"
        private const val TUNNEL_NAME = "SELOOM_VPN"

        @Volatile
        private var instance: WireGuardManager? = null

        fun getInstance(context: Context): WireGuardManager =
            instance ?: synchronized(this) {
                instance ?: WireGuardManager(context.applicationContext).also { instance = it }
            }
    }
}
