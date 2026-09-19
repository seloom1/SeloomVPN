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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan

@Composable
fun ServerSelectorCard(
    currentServerName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = NeonCyan.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xE6061B37),
                        Color(0xF2020D1F),
                        Color(0xE6061B37)
                    )
                )
            )
            .border(
                width = 1.8.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.9f),
                        ElectricBlue.copy(alpha = 0.95f),
                        NeonCyan.copy(alpha = 0.9f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .testTag("server_selector_card")
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Server Rack Icon
            ServerRackIcon(modifier = Modifier.size(38.dp))

            Spacer(modifier = Modifier.width(14.dp))

            // Middle: "اختر سيرفر" and server name
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "اختر سيرفر",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentServerName,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Chevron down
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Server",
                tint = NeonCyan,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ServerRackIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val unitHeight = h / 3.4f
        val gap = h / 12f
        val cornerRadius = 3.dp.toPx()

        for (i in 0..2) {
            val top = i * (unitHeight + gap)
            // Server unit box
            drawRoundRect(
                color = NeonCyan,
                topLeft = Offset(0f, top),
                size = Size(w, unitHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(width = 1.6.dp.toPx())
            )
            // Server unit fill
            drawRoundRect(
                color = Color(0xFF003D5B),
                topLeft = Offset(1.6.dp.toPx(), top + 1.6.dp.toPx()),
                size = Size(w - 3.2.dp.toPx(), unitHeight - 3.2.dp.toPx()),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
            // LED dots
            drawCircle(
                color = NeonCyan,
                radius = 2.dp.toPx(),
                center = Offset(w * 0.25f, top + (unitHeight / 2f))
            )
            drawCircle(
                color = if (i == 1) Color(0xFF10B981) else NeonCyan,
                radius = 2.dp.toPx(),
                center = Offset(w * 0.45f, top + (unitHeight / 2f))
            )
            // Slot line
            drawLine(
                color = NeonCyan.copy(alpha = 0.8f),
                start = Offset(w * 0.65f, top + (unitHeight / 2f)),
                end = Offset(w * 0.88f, top + (unitHeight / 2f)),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}
