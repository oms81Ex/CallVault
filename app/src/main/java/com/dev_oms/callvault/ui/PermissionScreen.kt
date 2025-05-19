package com.dev_oms.callvault.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(onRequestPermission: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("권한 안내") })
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("앱의 정상 동작을 위해 다음 권한이 필요합니다:")
                Spacer(modifier = Modifier.height(12.dp))
                Text("- 마이크(통화 녹음)")
                Text("- 저장소(파일 저장/관리)")
                Text("- 전화 상태(통화 감지)")
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRequestPermission) { Text("권한 요청하기") }
            }
        }
    )
} 