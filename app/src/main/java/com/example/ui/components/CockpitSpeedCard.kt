package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.vpn.VpnStatus
import java.util.Locale

@Composable
fun CockpitSpeedCard(
    vpnStatus: VpnStatus,
    downloadSpeed: Float,
    uploadSpeed: Float,
    onPowerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnStatus == VpnStatus.CONNECTED
    val isConnecting = vpnStatus == VpnStatus.CONNECTING
    val downloadDisplay = formatSpeed(if (isConnected) downloadSpeed else 0f)
    val uploadDisplay = formatSpeed(if (isConnected) uploadSpeed else 0f)

    // Subtle pulsing animation for active/connecting state
    val infiniteTransition = rememberInfiniteTransition(label = "powerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected || isConnecting) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Sci-fi outer container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = NeonCyan.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE605152D),
                        Color(0xF0020B19)
                    )
                )
            )
            .border(
                width = 1.8.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.8f),
                        ElectricBlue.copy(alpha = 0.9f),
                        NeonCyan.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT COLUMN: Download / التحميل
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Download Arrow Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF0099FF), Color(0xFF0055AA))
                            )
                        )
                        .border(1.2.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Download",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "التحميل",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = downloadDisplay.value,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                )

                Text(
                    text = downloadDisplay.unit,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cyan waveform
                WaveformCanvas(
                    color = NeonCyan,
                    isActive = isConnected,
                    height = 20.dp,
                    amplitude = 8f
                )
            }

            // CENTER COLUMN: Big Glowing Power Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1.2f)
                    .padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = NeonCyan),
                            onClick = onPowerClick
                        )
                        .testTag("power_connect_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow canvas
                    Canvas(modifier = Modifier.size(118.dp)) {
                        val strokeWidth = 3.5.dp.toPx()
                        val ringColor = when (vpnStatus) {
                            VpnStatus.CONNECTED -> NeonCyan
                            VpnStatus.CONNECTING -> Color(0xFFFACC15)
                            VpnStatus.ERROR -> Color(0xFFEF4444)
                            VpnStatus.DISCONNECTED -> Color(0xFF334155)
                        }

                        // Outer ring glow
                        drawCircle(
                            color = ringColor.copy(alpha = 0.25f),
                            radius = size.minDimension / 2f,
                            style = Stroke(width = strokeWidth * 2.5f)
                        )
                        // Sharp ring
                        drawCircle(
                            color = ringColor,
                            radius = (size.minDimension / 2f) - (strokeWidth / 2),
                            style = Stroke(width = strokeWidth)
                        )
                    }

                    // Inner Button Body
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when (vpnStatus) {
                                        VpnStatus.CONNECTED -> listOf(
                                            Color(0xFF003859),
                                            Color(0xFF001F33),
                                            Color(0xFF00101C)
                                        )
                                        VpnStatus.CONNECTING -> listOf(
                                            Color(0xFF4A3800),
                                            Color(0xFF2B2000),
                                            Color(0xFF140F00)
                                        )
                                        else -> listOf(
                                            Color(0xFF0F1E33),
                                            Color(0xFF08111D),
                                            Color(0xFF03070C)
                                        )
                                    }
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = if (isConnected) NeonCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Toggle VPN",
                            tint = when (vpnStatus) {
                                VpnStatus.CONNECTED -> NeonCyan
                                VpnStatus.CONNECTING -> Color(0xFFFDE047)
                                VpnStatus.ERROR -> Color(0xFFF87171)
                                VpnStatus.DISCONNECTED -> Color.White.copy(alpha = 0.85f)
                            },
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status text
                Text(
                    text = when (vpnStatus) {
                        VpnStatus.CONNECTED -> "متصل"
                        VpnStatus.CONNECTING -> "جاري الاتصال..."
                        VpnStatus.ERROR -> "خطأ في الاتصال"
                        VpnStatus.DISCONNECTED -> "غير متصل"
                    },
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (vpnStatus) {
                            VpnStatus.CONNECTED -> NeonCyan
                            VpnStatus.CONNECTING -> Color(0xFFFDE047)
                            VpnStatus.ERROR -> Color(0xFFEF4444)
                            VpnStatus.DISCONNECTED -> Color.White.copy(alpha = 0.7f)
                        },
                        shadow = if (isConnected) Shadow(
                            color = NeonCyan,
                            offset = Offset(0f, 0f),
                            blurRadius = 14f
                        ) else null
                    )
                )
            }

            // RIGHT COLUMN: Upload / الرفع
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Circular Upload Arrow Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF00E599), Color(0xFF008855))
                            )
                        )
                        .border(1.2.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "الرفع",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = uploadDisplay.value,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                )

                Text(
                    text = uploadDisplay.unit,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Green/Teal waveform
                WaveformCanvas(
                    color = NeonGreen,
                    isActive = isConnected,
                    height = 20.dp,
                    amplitude = 8f
                )
            }
        }
    }
}

private data class SpeedDisplay(val value: String, val unit: String)

private fun formatSpeed(speedKBps: Float): SpeedDisplay {
    val safeSpeed = speedKBps.coerceAtLeast(0f)
    return if (safeSpeed >= 1024f) {
        SpeedDisplay(String.format(Locale.US, "%.1f", safeSpeed / 1024f), "MB/s")
    } else {
        SpeedDisplay(String.format(Locale.US, "%.1f", safeSpeed), "KB/s")
    }
}
