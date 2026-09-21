package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.ServerRepository

/** Keeps the WireGuard process alive after the activity is closed or removed from recents. */
class VpnForegroundService : Service() {

    private val manager by lazy { WireGuardManager.getInstance(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If Android recreates this service, restore the last selected tunnel.
        if (manager.vpnStatus.value == VpnStatus.DISCONNECTED || manager.vpnStatus.value == VpnStatus.ERROR) {
            val repository = ServerRepository(applicationContext)
            val servers = repository.getSavedServers()
            val selected = repository.getSelectedServerId()?.let { id -> servers.find { it.id == id } }
                ?: servers.firstOrNull()
            selected?.let { manager.connect(it) }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Do not stop the service when the launcher task is swiped away.
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_vpn_1789770995926)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("حماية WireGuard تعمل في الخلفية")
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openAppIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "حالة VPN",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "إشعار استمرار اتصال VPN في الخلفية" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "com.aistudio.wireguardvpn.seloom.START_VPN_SERVICE"
        private const val CHANNEL_ID = "seloom_vpn_status"
        private const val NOTIFICATION_ID = 1001
    }
}
