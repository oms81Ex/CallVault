package com.dev_oms.callvault

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import com.dev_oms.callvault.SettingsManager
import android.os.StatFs

// Repository for managing recordings (stub for now, can be expanded)
class RecordingRepository(private val context: Context) {
    // 샘플 데이터는 주석 처리
    // private val sampleData = mutableListOf(
    //     Recording("녹음_20240601_120000.mp3", "/storage/emulated/0/CallVault/녹음_20240601_120000.mp3", "2024-06-01 12:00", "03:21", "INCOMING", "010-1234-5678"),
    //     ...
    // )

    // 실제 파일 시스템에서 녹음 파일 목록을 반환
    suspend fun getAllRecordings(): List<Recording> {
        val recordings = mutableListOf<Recording>()
        val dir = File(context.getExternalFilesDir(null), "recordings")
        if (dir.exists()) {
            dir.listFiles()?.forEach { file ->
                // 파일명에서 메타데이터 추출(예: "IN_01012345678_20240601_120000.mp3")
                val name = file.name
                val (callType, phone, date, time) = parseFileName(name)
                val duration = "--:--" // TODO: 실제 파일에서 duration 추출
                val dateStr = "$date $time"
                recordings.add(
                    Recording(
                        fileName = name,
                        filePath = file.absolutePath,
                        date = dateStr,
                        duration = duration,
                        callType = callType,
                        phoneNumber = phone
                    )
                )
            }
        }
        return recordings
    }

    suspend fun deleteRecording(recording: Recording) {
        val file = File(recording.filePath)
        if (file.exists()) file.delete()
    }

    fun shareRecording(recording: Recording): Intent {
        val file = File(recording.filePath)
        val uri = Uri.fromFile(file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
    }

    // 파일명에서 메타데이터 추출 (예: "IN_01012345678_20240601_120000.mp3")
    private fun parseFileName(name: String): Quad<String, String, String, String> {
        // 예시: IN_01012345678_20240601_120000.mp3
        val parts = name.removeSuffix(".mp3").split("_")
        return if (parts.size >= 4) {
            val callType = if (parts[0] == "IN") "INCOMING" else "OUTGOING"
            val phone = parts[1]
            val date = parts[2]
            val time = parts[3]
            Quad(callType, phone, date, time)
        } else {
            Quad("UNKNOWN", "", "", "")
        }
    }

    // 4개 값 반환용 데이터 클래스
    data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    // 자동 삭제: 오래된 파일/용량 초과 파일 삭제
    suspend fun autoDeleteOldRecordings() {
        val settings = SettingsManager.getInstance(context)
        val dir = File(context.getExternalFilesDir(null), "recordings")
        if (!dir.exists()) return
        val files = dir.listFiles()?.filter { it.isFile } ?: return
        val now = System.currentTimeMillis()
        val maxDays = settings.autoDeleteDays
        val maxSizeMb = settings.autoDeleteSizeMb
        // 1. 오래된 파일 삭제
        files.forEach { file ->
            val lastModified = file.lastModified()
            val days = (now - lastModified) / (1000 * 60 * 60 * 24)
            if (days > maxDays) {
                file.delete()
            }
        }
        // 2. 용량 초과 시 오래된 파일부터 삭제
        val sortedFiles = dir.listFiles()?.filter { it.isFile }?.sortedBy { it.lastModified() } ?: return
        var totalSizeMb = sortedFiles.sumOf { it.length() } / (1024 * 1024)
        for (file in sortedFiles) {
            if (totalSizeMb <= maxSizeMb) break
            file.delete()
            totalSizeMb -= file.length() / (1024 * 1024)
        }
    }

    // TODO: Add methods for add/save, update, etc.
} 