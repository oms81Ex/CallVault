package com.dev_oms.callvault.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dev_oms.callvault.Recording

@Composable
fun DetailScreen(recording: Recording, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("녹음 파일 상세") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("파일명: ${recording.fileName}")
                Text("날짜: ${recording.date}")
                Text("길이: ${recording.duration}")
                Text("통화유형: ${recording.callType}")
                Text("전화번호: ${recording.phoneNumber}")
                Text("경로: ${recording.filePath}")
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("닫기") }
        }
    )
} 