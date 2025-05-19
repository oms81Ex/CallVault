package com.dev_oms.callvault

import android.content.Context
import android.content.SharedPreferences

class SettingsManager private constructor(context: Context) {
    companion object {
        private const val PREF_NAME = "callvault_settings"
        private var instance: SettingsManager? = null
        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Keys
    private val KEY_SAVE_LOCATION = "save_location"
    private val KEY_FILE_NAMING = "file_naming"
    private val KEY_QUALITY = "quality"
    private val KEY_AUTO_DELETE_DAYS = "auto_delete_days"
    private val KEY_AUTO_DELETE_SIZE = "auto_delete_size"
    private val KEY_AUTO_RECORD = "auto_record"
    private val KEY_SHOW_NOTIFICATION = "show_notification"

    // 기본값
    private val DEFAULT_SAVE_LOCATION = "내부 저장소"
    private val DEFAULT_FILE_NAMING = "{CALL_TYPE}_{PHONE}_{DATE}_{TIME}"
    private val DEFAULT_QUALITY = "기본"
    private val DEFAULT_AUTO_DELETE_DAYS = 30
    private val DEFAULT_AUTO_DELETE_SIZE = 1024 // MB 단위(1GB)
    private val DEFAULT_AUTO_RECORD = true
    private val DEFAULT_SHOW_NOTIFICATION = true

    var saveLocation: String
        get() = prefs.getString(KEY_SAVE_LOCATION, DEFAULT_SAVE_LOCATION) ?: DEFAULT_SAVE_LOCATION
        set(value) = prefs.edit().putString(KEY_SAVE_LOCATION, value).apply()

    var fileNaming: String
        get() = prefs.getString(KEY_FILE_NAMING, DEFAULT_FILE_NAMING) ?: DEFAULT_FILE_NAMING
        set(value) = prefs.edit().putString(KEY_FILE_NAMING, value).apply()

    var quality: String
        get() = prefs.getString(KEY_QUALITY, DEFAULT_QUALITY) ?: DEFAULT_QUALITY
        set(value) = prefs.edit().putString(KEY_QUALITY, value).apply()

    var autoDeleteDays: Int
        get() = prefs.getInt(KEY_AUTO_DELETE_DAYS, DEFAULT_AUTO_DELETE_DAYS)
        set(value) = prefs.edit().putInt(KEY_AUTO_DELETE_DAYS, value).apply()

    var autoDeleteSizeMb: Int
        get() = prefs.getInt(KEY_AUTO_DELETE_SIZE, DEFAULT_AUTO_DELETE_SIZE)
        set(value) = prefs.edit().putInt(KEY_AUTO_DELETE_SIZE, value).apply()

    var autoRecord: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RECORD, DEFAULT_AUTO_RECORD)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_RECORD, value).apply()

    var showNotification: Boolean
        get() = prefs.getBoolean(KEY_SHOW_NOTIFICATION, DEFAULT_SHOW_NOTIFICATION)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_NOTIFICATION, value).apply()
} 