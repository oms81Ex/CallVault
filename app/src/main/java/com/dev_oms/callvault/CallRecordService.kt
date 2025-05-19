package com.dev_oms.callvault

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CallRecordService : Service() {
    companion object {
        const val CHANNEL_ID = "call_record_channel"
        const val NOTIFICATION_ID = 1001
    }

    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var isRecording = false
    private var phoneNumber: String = ""
    private var callType: String = ""

    private val callReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    callType = "IN"
                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "unknown"
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    // 수신/발신 모두 OFFHOOK에서 녹음 시작
                    startRecording()
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    // 통화 종료 시 녹음 종료
                    stopRecording()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver(callReceiver, IntentFilter("android.intent.action.PHONE_STATE"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("통화 녹음 대기 중")
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(callReceiver)
        stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("통화 녹음")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "통화 녹음",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun startRecording() {
        if (isRecording) return
        try {
            val settings = SettingsManager.getInstance(this)
            val dir = when (settings.saveLocation) {
                "외부 저장소" -> getExternalFilesDir(null)
                else -> filesDir
            }
            val recordingsDir = File(dir, "recordings")
            if (!recordingsDir.exists()) recordingsDir.mkdirs()
            // 저장 공간 체크 (100MB 이하 남으면 녹음 금지 및 알림)
            val freeMb = recordingsDir.usableSpace / (1024 * 1024)
            if (freeMb < 100) {
                showErrorNotification("저장 공간 부족: ${freeMb}MB 남음")
                return
            }
            val now = Date()
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timeStr = sdf.format(now)
            val fileName = settings.fileNaming
                .replace("{CALL_TYPE}", callType)
                .replace("{PHONE}", phoneNumber)
                .replace("{DATE}", timeStr.substring(0,8))
                .replace("{TIME}", timeStr.substring(9)) + ".mp3"
            val file = File(recordingsDir, fileName)
            currentFile = file
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            updateNotification("녹음 중: $phoneNumber")
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorNotification("녹음 시작 실패: ${e.message}")
        }
    }

    private fun stopRecording() {
        if (!isRecording) return
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            updateNotification("녹음 완료: $phoneNumber")
            showCompleteNotification(currentFile?.name ?: "녹음 파일")
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorNotification("녹음 종료 실패: ${e.message}")
        }
    }

    private fun updateNotification(content: String) {
        val notification = buildNotification(content)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun showCompleteNotification(fileName: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("녹음 완료")
            .setContentText(fileName)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun showErrorNotification(msg: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("녹음 에러")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID + 2, notification)
    }
} 