package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.VpnServer
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ServerSelectionDialog(
    servers: List<VpnServer>,
    selectedServer: VpnServer,
    onSelectServer: (VpnServer) -> Unit,
    onDeleteServer: (VpnServer) -> Unit,
    onAddServer: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var isAddingMode by remember { mutableStateOf(false) }
    var linkInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val sampleUserLink = "wireguard://AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D@engage.cloudflareclient.com:2408?address=172.16.0.2%2F32%2C2606%3A4700%3A110%3A8d70%3A8df1%3A6e3d%3A693b%3Aea40%2F128&publickey=bmXOC%2BF1FxEMF9dyiK2H5%2F1SUtzH0JuVo51h2wPfgyo%3D&privatekey=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA%3D#Seloom VPN%20BALLY"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.6.dp, NeonCyan, RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (isAddingMode) "إضافة سيرفر WireGuard" else "سيرفرات WireGuard",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isAddingMode) {
                    // ADD NEW SERVER SCREEN
                    Text(
                        text = "الصق رابط WireGuard أو إعدادات التكوين:",
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = {
                            linkInput = it
                            errorMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("wireguard_link_input"),
                        placeholder = {
                            Text(
                                "wireguard://... أو [Interface]...",
                                color = TextMuted.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E3A5F),
                            focusedContainerColor = Color(0xFF030A17),
                            unfocusedContainerColor = Color(0xFF030A17)
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons: Paste from clipboard / Quick Sample
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clip = clipboardManager.getText()
                                if (clip != null) {
                                    linkInput = clip.text
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2442))
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("لصق من الحافظة", fontSize = 11.sp, color = NeonCyan)
                        }

                        Button(
                            onClick = {
                                linkInput = sampleUserLink
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A40))
                        ) {
                            Text("رابط التجربة", fontSize = 11.sp, color = Color(0xFFC084FC))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Save and Cancel buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isAddingMode = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Text("إلغاء", color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (linkInput.isBlank()) {
                                    errorMessage = "يرجى إدخال الرابط أولاً"
                                    return@Button
                                }
                                val success = onAddServer(linkInput)
                                if (!success) {
                                    errorMessage = "صيغة الرابط غير صحيحة، يرجى التحقق"
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("save_server_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("حفظ وتفعيل", color = Color(0xFF020919), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // LIST OF SERVERS
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(servers, key = { it.id }) { server ->
                            val isCurrent = server.id == selectedServer.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrent) Color(0xFF003D66) else Color(0xFF05152D)
                                    )
                                    .border(
                                        width = if (isCurrent) 1.5.dp else 0.8.dp,
                                        color = if (isCurrent) NeonCyan else Color(0xFF1E3A5F),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectServer(server) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (server.isCustom) {
                                        IconButton(
                                            onClick = { onDeleteServer(server) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Dns,
                                            contentDescription = null,
                                            tint = if (isCurrent) NeonCyan else TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = server.name,
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = server.endpoint,
                                            style = TextStyle(
                                                fontSize = 11.sp,
                                                color = NeonCyan,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                        if (server.note.isNotEmpty()) {
                                            Text(
                                                text = server.note,
                                                style = TextStyle(
                                                    fontSize = 10.sp,
                                                    color = TextMuted
                                                )
                                            )
                                        }
                                    }

                                    RadioButton(
                                        selected = isCurrent,
                                        onClick = { onSelectServer(server) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = NeonCyan,
                                            unselectedColor = TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Add server button
                    Button(
                        onClick = { isAddingMode = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_server_link_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(0xFF020919)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إضافة رابط WireGuard جديد",
                            color = Color(0xFF020919),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
