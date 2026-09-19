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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BalyBlue
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted

@Composable
fun DeveloperBrandCard(
    onTelegramClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = ElectricBlue.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xE605152D),
                        Color(0xF0020B19),
                        Color(0xE605152D)
                    )
                )
            )
            .border(
                width = 1.6.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.85f),
                        NeonCyan.copy(alpha = 0.85f),
                        ElectricBlue.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT PART: POWERED BY SELOOM1@ المطور
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stylized neon 'S' circle logo
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF0055AA), Color(0xFF001A33))
                            )
                        )
                        .border(1.5.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "POWERED BY",
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "SELOOM1@",
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "المطور",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted
                        )
                    )
                }
            }

            // Vertical divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            // RIGHT PART: Telegram Channel Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onTelegramClick() }
            ) {
                // Circular Telegram Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF29B6F6), Color(0xFF0288D1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Telegram",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "قناة التلكرام",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "https://t.me/freevpsiraq",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FooterSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown vector icon
        CrownIcon(modifier = Modifier.size(24.dp))

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "SELOOM1@",
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "— سرعتك .. هدفنا —",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeonCyan.copy(alpha = 0.85f)
            )
        )
    }
}

@Composable
fun CrownIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.8f)
            lineTo(w * 0.9f, h * 0.8f)
            lineTo(w * 0.95f, h * 0.35f)
            lineTo(w * 0.7f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.2f)
            lineTo(w * 0.3f, h * 0.55f)
            lineTo(w * 0.05f, h * 0.35f)
            close()
        }
        drawPath(
            path = path,
            color = Color(0xFF38BDF8),
            style = Stroke(
                width = 1.8.dp.toPx(),
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
        // Little top circle jewels
        drawCircle(color = NeonCyan, radius = 1.8.dp.toPx(), center = Offset(w * 0.5f, h * 0.12f))
        drawCircle(color = NeonCyan, radius = 1.5.dp.toPx(), center = Offset(w * 0.05f, h * 0.27f))
        drawCircle(color = NeonCyan, radius = 1.5.dp.toPx(), center = Offset(w * 0.95f, h * 0.27f))
    }
}
