package com.dev_oms.callvault

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SettingsManagerTest {
    private lateinit var context: Context
    private lateinit var settings: SettingsManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        settings = SettingsManager.getInstance(context)
        // 초기화: 모든 값 기본값으로 리셋
        settings.saveLocation = "내부 저장소"
        settings.fileNaming = "{CALL_TYPE}_{PHONE}_{DATE}_{TIME}"
        settings.quality = "기본"
        settings.autoDeleteDays = 30
        settings.autoDeleteSizeMb = 1024
        settings.autoRecord = true
        settings.showNotification = true
    }

    @Test
    fun testDefaultValues() {
        assertEquals("내부 저장소", settings.saveLocation)
        assertEquals("{CALL_TYPE}_{PHONE}_{DATE}_{TIME}", settings.fileNaming)
        assertEquals("기본", settings.quality)
        assertEquals(30, settings.autoDeleteDays)
        assertEquals(1024, settings.autoDeleteSizeMb)
        assertTrue(settings.autoRecord)
        assertTrue(settings.showNotification)
    }

    @Test
    fun testSetAndGetValues() {
        settings.saveLocation = "외부 저장소"
        settings.fileNaming = "TEST_{DATE}"
        settings.quality = "고음질"
        settings.autoDeleteDays = 10
        settings.autoDeleteSizeMb = 500
        settings.autoRecord = false
        settings.showNotification = false

        assertEquals("외부 저장소", settings.saveLocation)
        assertEquals("TEST_{DATE}", settings.fileNaming)
        assertEquals("고음질", settings.quality)
        assertEquals(10, settings.autoDeleteDays)
        assertEquals(500, settings.autoDeleteSizeMb)
        assertFalse(settings.autoRecord)
        assertFalse(settings.showNotification)
    }
} 