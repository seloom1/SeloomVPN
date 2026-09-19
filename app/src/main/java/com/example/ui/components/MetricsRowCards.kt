package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlagBlack
import com.example.ui.theme.FlagGreen
import com.example.ui.theme.FlagRed
import com.example.ui.theme.FlagWhite
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted

@Composable
fun MetricsRowCards(
    pingMs: Int,
    ipAddress: String,
    country: String,
    countryCode: String,
    onCopyIp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // CARD 1: PING / البنك
        Box(
            modifier = Modifier
                .weight(1f)
                .height(115.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonPurple.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xE60D122B), Color(0xF0060917))
                    )
                )
                .border(1.5.dp, NeonPurple.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFA855F7), Color(0xFF6B21A8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Ping",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "البنك",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$pingMs",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ms",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Purple wave line
                WaveformCanvas(
                    color = NeonPurple,
                    isActive = true,
                    height = 14.dp,
                    amplitude = 5f
                )
            }
        }

        // CARD 2: IP / الآي بي
        Box(
            modifier = Modifier
                .weight(1f)
                .height(115.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = ElectricBlue.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xE605152D), Color(0xF0020B19))
                    )
                )
                .border(1.5.dp, ElectricBlue.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                .clickable { onCopyIp() }
                .testTag("ip_address_card")
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0EA5E9), Color(0xFF0369A1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "IP",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الآي بي",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ipAddress,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy IP",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "انقر للنسخ",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = NeonCyan.copy(alpha = 0.7f)
                    )
                )
            }
        }

        // CARD 3: COUNTRY / الدولة
        Box(
            modifier = Modifier
                .weight(1f)
                .height(115.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NeonCyan.copy(alpha = 0.35f))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xE605172E), Color(0xF0020B19))
                    )
                )
                .border(1.5.dp, NeonCyan.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountryFlagBadge(
                        countryCode = countryCode,
                        modifier = Modifier.size(width = 38.dp, height = 24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "الدولة",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = country,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "نشط",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )
            }
        }
    }
}

@Composable
fun CountryFlagBadge(countryCode: String, modifier: Modifier = Modifier) {
    val normalized = countryCode.trim().uppercase()
    val flag = if (normalized.length == 2 && normalized.all { it in 'A'..'Z' }) {
        normalized.map { Character.toChars(0x1F1E6 + (it.code - 'A'.code)).concatToString() }.joinToString("")
    } else {
        "🌐"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF10243C))
            .border(0.8.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = flag, fontSize = 17.sp, maxLines = 1)
    }
}

@Composable
fun IraqiFlagBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(0.8.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val h = size.height / 3f
            // Red top stripe
            drawRect(
                color = FlagRed,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, h)
            )
            // White middle stripe
            drawRect(
                color = FlagWhite,
                topLeft = Offset(0f, h),
                size = Size(size.width, h)
            )
            // Black bottom stripe
            drawRect(
                color = FlagBlack,
                topLeft = Offset(0f, h * 2),
                size = Size(size.width, h)
            )
            // Arabic Takbir in green in center
            drawCircle(
                color = FlagGreen,
                radius = 2.5.dp.toPx(),
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }
    }
}
