package com.dev_oms.callvault.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.dev_oms.callvault.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, context: Context) {
    val settings = remember { SettingsManager.getInstance(context) }
    var autoRecord by remember { mutableStateOf(settings.autoRecord) }
    var showNotification by remember { mutableStateOf(settings.showNotification) }
    var saveLocation by remember { mutableStateOf(settings.saveLocation) }
    var fileNaming by remember { mutableStateOf(settings.fileNaming) }
    var quality by remember { mutableStateOf(settings.quality) }
    var autoDeleteDays by remember { mutableStateOf(settings.autoDeleteDays.toString()) }
    var autoDeleteSize by remember { mutableStateOf(settings.autoDeleteSizeMb.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("설정") })
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("자동 녹음", modifier = Modifier.weight(1f))
                    Switch(checked = autoRecord, onCheckedChange = {
                        autoRecord = it
                        settings.autoRecord = it
                    })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("녹음 알림 표시", modifier = Modifier.weight(1f))
                    Switch(checked = showNotification, onCheckedChange = {
                        showNotification = it
                        settings.showNotification = it
                    })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("저장 위치")
                Row {
                    RadioButton(selected = saveLocation == "내부 저장소", onClick = {
                        saveLocation = "내부 저장소"
                        settings.saveLocation = "내부 저장소"
                    })
                    Text("내부 저장소", modifier = Modifier.padding(end = 16.dp))
                    RadioButton(selected = saveLocation == "외부 저장소", onClick = {
                        saveLocation = "외부 저장소"
                        settings.saveLocation = "외부 저장소"
                    })
                    Text("외부 저장소")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = fileNaming,
                    onValueChange = {
                        fileNaming = it
                        settings.fileNaming = it
                    },
                    label = { Text("파일명 규칙") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = quality,
                    onValueChange = {
                        quality = it
                        settings.quality = it
                    },
                    label = { Text("품질") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = autoDeleteDays,
                    onValueChange = {
                        autoDeleteDays = it.filter { c -> c.isDigit() }
                        settings.autoDeleteDays = autoDeleteDays.toIntOrNull() ?: 30
                    },
                    label = { Text("자동 삭제(일)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = autoDeleteSize,
                    onValueChange = {
                        autoDeleteSize = it.filter { c -> c.isDigit() }
                        settings.autoDeleteSizeMb = autoDeleteSize.toIntOrNull() ?: 1024
                    },
                    label = { Text("자동 삭제(최대 용량 MB)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onBack, modifier = Modifier.align(Alignment.End)) {
                    Text("뒤로가기")
                }
            }
        }
    )
} 