package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.InstalledApp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted

@Composable
fun AppBlacklistDialog(
    apps: List<InstalledApp>,
    selectedPackages: Set<String>,
    onSave: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember(selectedPackages) { mutableStateOf(selectedPackages) }
    var searchQuery by remember { mutableStateOf("") }
    var showSystemApps by remember { mutableStateOf(false) }
    val filteredApps = remember(apps, searchQuery, showSystemApps) {
        val query = searchQuery.trim().lowercase()
        apps.filter { app ->
            (showSystemApps || !app.isSystemApp) &&
                (query.isEmpty() || app.label.lowercase().contains(query) || app.packageName.lowercase().contains(query))
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(CyberCardBg)
                .border(1.6.dp, NeonCyan, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("قائمة التطبيقات المستثناة", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "ابحث عن تطبيق وحدده حتى يستخدم الإنترنت العادي ولا يمر عبر الـVPN.",
                color = TextMuted,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "ملاحظة: هنا تختار تطبيقات الباقة المجانية التي اخترتها حتى يبقى الإنترنت فيها ثابتًا والبنك ثابتًا.",
                color = Color(0xFFFFD166),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("ابحث باسم التطبيق أو الحزمة", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF2A5A87),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF05152D))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (showSystemApps) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        if (showSystemApps) "إخفاء تطبيقات النظام" else "إظهار تطبيقات النظام",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Switch(
                    checked = showSystemApps,
                    onCheckedChange = { showSystemApps = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF020919),
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF183451)
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (filteredApps.isEmpty()) {
                    item {
                        Text(
                            if (showSystemApps) "لا توجد تطبيقات مطابقة للبحث" else "لا توجد تطبيقات مستخدم مطابقة للبحث",
                            color = TextMuted,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(filteredApps, key = { it.packageName }) { app ->
                    val checked = app.packageName in selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (checked) Color(0xFF003D66) else Color(0xFF05152D))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(packageName = app.packageName, label = app.label)
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                selected = if (it) selected + app.packageName else selected - app.packageName
                            },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan, checkmarkColor = Color(0xFF020919))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.label, color = Color.White, fontSize = 14.sp, maxLines = 1)
                            Text(app.packageName, color = TextMuted, fontSize = 10.sp, maxLines = 1)
                        }
                        if (checked) Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onSave(selected) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("حفظ وتطبيق (${selected.size})", color = Color(0xFF020919), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AppIcon(packageName: String, label: String) {
    val context = LocalContext.current
    val bitmap = remember(packageName) {
        runCatching { drawableToBitmap(context.packageManager.getApplicationIcon(packageName)) }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "أيقونة $label",
            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp))
        )
    } else {
        Icon(Icons.Default.Shield, contentDescription = "أيقونة $label", tint = NeonCyan, modifier = Modifier.size(42.dp))
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }
}
