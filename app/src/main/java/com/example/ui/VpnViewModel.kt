package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ServerRepository
import com.example.model.VpnServer
import com.example.model.InstalledApp
import com.example.vpn.SpeedMetrics
import com.example.vpn.VpnStatus
import com.example.vpn.WireGuardLinkParser
import com.example.vpn.WireGuardManager
import com.example.vpn.VpnForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ServerRepository(application)
    private val wireGuardManager = WireGuardManager.getInstance(application)

    val vpnStatus: StateFlow<VpnStatus> = wireGuardManager.vpnStatus
    val metrics: StateFlow<SpeedMetrics> = wireGuardManager.metrics

    private val _serverList = MutableStateFlow(repository.getSavedServers())
    val serverList: StateFlow<List<VpnServer>> = _serverList.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer>(
        repository.getSelectedServerId()?.let { id ->
            _serverList.value.find { it.id == id }
        } ?: _serverList.value.first()
    )
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _showServerDialog = MutableStateFlow(false)
    val showServerDialog: StateFlow<Boolean> = _showServerDialog.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private val _showBlacklistDialog = MutableStateFlow(false)
    val showBlacklistDialog: StateFlow<Boolean> = _showBlacklistDialog.asStateFlow()

    private val _excludedApplications = MutableStateFlow(repository.getExcludedApplications())
    val excludedApplications: StateFlow<Set<String>> = _excludedApplications.asStateFlow()

    val installedApplications: List<InstalledApp> = application.packageManager
        .getInstalledApplications(PackageManager.GET_META_DATA)
        .asSequence()
        .filter { it.packageName != application.packageName }
        .filter { application.packageManager.getLaunchIntentForPackage(it.packageName) != null }
        .map { InstalledApp(
            packageName = it.packageName,
            label = application.packageManager.getApplicationLabel(it).toString(),
            isSystemApp = (it.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM != 0) ||
                (it.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0)
        ) }
        .sortedBy { it.label.lowercase() }
        .toList()

    fun setServerDialogVisible(visible: Boolean) {
        _showServerDialog.value = visible
    }

    fun setAddDialogVisible(visible: Boolean) {
        _showAddDialog.value = visible
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun setBlacklistDialogVisible(visible: Boolean) {
        _showBlacklistDialog.value = visible
    }

    fun saveExcludedApplications(packages: Set<String>) {
        val cleanPackages = packages.filter { it != getApplication<Application>().packageName }.toSet()
        repository.setExcludedApplications(cleanPackages)
        _excludedApplications.value = cleanPackages
        _showBlacklistDialog.value = false
        _feedbackMessage.value = "تم حفظ التطبيقات المستثناة"
        if (vpnStatus.value == VpnStatus.CONNECTED) {
            wireGuardManager.disconnect()
            viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                wireGuardManager.connect(_selectedServer.value)
            }
        }
    }

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        repository.setSelectedServerId(server.id)
        if (vpnStatus.value == VpnStatus.CONNECTED) {
            wireGuardManager.disconnect()
            viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                wireGuardManager.connect(server)
            }
        }
        _showServerDialog.value = false
    }

    fun addServerFromText(text: String): Boolean {
        val parsed = WireGuardLinkParser.parse(text)
        return if (parsed != null) {
            repository.addCustomServer(parsed)
            _serverList.value = repository.getSavedServers()
            selectServer(parsed)
            _showAddDialog.value = false
            _feedbackMessage.value = "تمت إضافة سيرفر: ${parsed.name}"
            true
        } else {
            _feedbackMessage.value = "صيغة الرابط غير صحيحة، يرجى التأكد من رابط WireGuard"
            false
        }
    }

    fun deleteServer(server: VpnServer) {
        if (!server.isCustom) return
        repository.deleteCustomServer(server.id)
        _serverList.value = repository.getSavedServers()
        if (_selectedServer.value.id == server.id) {
            _selectedServer.value = _serverList.value.first()
            repository.setSelectedServerId(_selectedServer.value.id)
        }
        _feedbackMessage.value = "تم حذف السيرفر"
    }

    fun toggleConnection(onRequireVpnPermission: (Intent) -> Unit) {
        when (vpnStatus.value) {
            VpnStatus.CONNECTED, VpnStatus.CONNECTING -> {
                wireGuardManager.disconnect()
                getApplication<Application>().stopService(
                    Intent(getApplication(), VpnForegroundService::class.java)
                )
            }
            VpnStatus.DISCONNECTED, VpnStatus.ERROR -> {
                val vpnIntent = VpnService.prepare(getApplication())
                if (vpnIntent != null) {
                    onRequireVpnPermission(vpnIntent)
                } else {
                    connectNow()
                }
            }
        }
    }

    fun onVpnPermissionGranted() {
        connectNow()
    }

    private fun connectNow() {
        ContextCompat.startForegroundService(
            getApplication(),
            Intent(getApplication(), VpnForegroundService::class.java)
                .setAction(VpnForegroundService.ACTION_START)
        )
        wireGuardManager.connect(_selectedServer.value)
    }

    fun copyIpAddress(ip: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("IP Address", ip)
        clipboard.setPrimaryClip(clip)
        _feedbackMessage.value = "تم نسخ الآي بي: $ip"
    }

    fun openTelegramChannel(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/freevpsiraq"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            _feedbackMessage.value = "قناة التلكرام: https://t.me/freevpsiraq"
        }
    }
}
