package com.example.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.CockpitSpeedCard
import com.example.ui.components.DeveloperBrandCard
import com.example.ui.components.FooterSection
import com.example.ui.components.HeaderSection
import com.example.ui.components.MetricsRowCards
import com.example.ui.components.ServerSelectionDialog
import com.example.ui.components.ServerSelectorCard
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBgDeep

@Composable
fun VpnScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vpnStatus by viewModel.vpnStatus.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val serverList by viewModel.serverList.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val showServerDialog by viewModel.showServerDialog.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // VPN Permission Launcher
    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onVpnPermissionGranted()
        }
    }

    // Show feedback messages in snackbar
    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.cyber_bg_1789771056816),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.85f
            )

            // Sci-fi gradient overlay to guarantee readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xB3020919),
                                Color(0x66030F24),
                                Color(0x99020A1A),
                                Color(0xE601050F)
                            )
                        )
                    )
            )

            // Main Scrollable HUD Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header (oodi badge | Seloom VPN | WireGuard VPN Shield + Subheader)
                HeaderSection()

                Spacer(modifier = Modifier.height(4.dp))

                // Central Cockpit HUD Card (Download | Power Button | Upload)
                CockpitSpeedCard(
                    vpnStatus = vpnStatus,
                    downloadSpeed = metrics.downloadSpeedMbps,
                    uploadSpeed = metrics.uploadSpeedMbps,
                    onPowerClick = {
                        viewModel.toggleConnection { intent ->
                            vpnLauncher.launch(intent)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Metrics Row: Ping (البنك) | IP (الآي بي) | Country (الدولة)
                MetricsRowCards(
                pingMs = metrics.pingMs,
                ipAddress = metrics.ipAddress,
                country = metrics.country,
                countryCode = metrics.countryCode,
                onCopyIp = {
                        viewModel.copyIpAddress(metrics.ipAddress)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Server Selector Card (اختر سيرفر)
                ServerSelectorCard(
                    currentServerName = selectedServer.name,
                    onClick = {
                        viewModel.setServerDialogVisible(true)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Developer Brand & Telegram Card
                DeveloperBrandCard(
                    onTelegramClick = {
                        viewModel.openTelegramChannel(context)
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Footer Section (Crown SELOOM1@ — سرعتك .. هدفنا —)
                FooterSection()

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Server Selection / Link Import Dialog
            if (showServerDialog) {
                ServerSelectionDialog(
                    servers = serverList,
                    selectedServer = selectedServer,
                    onSelectServer = { server ->
                        viewModel.selectServer(server)
                    },
                    onDeleteServer = { server ->
                        viewModel.deleteServer(server)
                    },
                    onAddServer = { text ->
                        viewModel.addServerFromText(text)
                    },
                    onDismiss = {
                        viewModel.setServerDialogVisible(false)
                    }
                )
            }
        }
    }
}
