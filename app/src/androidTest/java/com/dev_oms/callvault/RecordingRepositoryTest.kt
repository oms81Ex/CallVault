package com.dev_oms.callvault

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class RecordingRepositoryTest {
    private lateinit var context: Context
    private lateinit var repo: RecordingRepository
    private lateinit var recordingsDir: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        repo = RecordingRepository(context)
        // recordings 디렉토리 생성
        recordingsDir = File(context.getExternalFilesDir(null), "recordings")
        if (!recordingsDir.exists()) recordingsDir.mkdirs()
        // 테스트용 더미 파일 생성 (3개, 2개는 오래된 파일, 1개는 최근 파일)
        val oldFile1 = File(recordingsDir, "IN_01012345678_20230101_120000.mp3")
        val oldFile2 = File(recordingsDir, "IN_01012345678_20230102_120000.mp3")
        val newFile = File(recordingsDir, "IN_01012345678_20240601_120000.mp3")
        oldFile1.writeBytes(ByteArray(1024 * 1024 * 2)) // 2MB
        oldFile2.writeBytes(ByteArray(1024 * 1024 * 2)) // 2MB
        newFile.writeBytes(ByteArray(1024 * 1024 * 2)) // 2MB
        // 오래된 파일로 타임스탬프 조정
        oldFile1.setLastModified(System.currentTimeMillis() - 100L * 24 * 60 * 60 * 1000) // 100일 전
        oldFile2.setLastModified(System.currentTimeMillis() - 50L * 24 * 60 * 60 * 1000) // 50일 전
        newFile.setLastModified(System.currentTimeMillis())
        // SettingsManager 값 조정 (30일, 5MB)
        val settings = SettingsManager.getInstance(context)
        settings.autoDeleteDays = 30
        settings.autoDeleteSizeMb = 5
    }

    @After
    fun tearDown() {
        // recordings 디렉토리 정리
        recordingsDir.listFiles()?.forEach { it.delete() }
    }

    @Test
    fun testAutoDeleteOldRecordings() = runBlocking {
        // 실행 전 파일 3개
        assertEquals(3, recordingsDir.listFiles()?.size)
        // 자동 삭제 실행
        repo.autoDeleteOldRecordings()
        // 100일, 50일 전 파일은 삭제, 최근 파일만 남아야 함
        val files = recordingsDir.listFiles()?.map { it.name } ?: emptyList()
        assertEquals(1, files.size)
        assertTrue(files[0].contains("20240601"))
    }

    @Test
    fun testAutoDeleteBySize() = runBlocking {
        // SettingsManager 값 조정 (1일, 4MB) -> 용량 초과로 2개 삭제
        val settings = SettingsManager.getInstance(context)
        settings.autoDeleteDays = 1
        settings.autoDeleteSizeMb = 4
        // 자동 삭제 실행
        repo.autoDeleteOldRecordings()
        val files = recordingsDir.listFiles()?.map { it.name } ?: emptyList()
        // 2MB씩 3개(6MB) -> 2개 삭제되어 1개만 남아야 함
        assertEquals(1, files.size)
    }
} 