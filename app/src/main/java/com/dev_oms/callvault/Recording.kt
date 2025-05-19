package com.dev_oms.callvault

// 통화 녹음 파일의 메타데이터를 모두 포함
// filePath: 실제 파일 경로, callType: 수신/발신, phoneNumber: 상대방 번호, ...
data class Recording(
    val fileName: String,
    val filePath: String, // 파일의 실제 경로
    val date: String,
    val duration: String,
    val callType: String, // "INCOMING" or "OUTGOING"
    val phoneNumber: String // 상대방 전화번호
) 