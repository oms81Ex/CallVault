package com.dev_oms.callvault

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// import the Recording data class
import com.dev_oms.callvault.Recording

class MainViewModel(
    context: Context
) : ViewModel() {
    private val repository = RecordingRepository(context)
    // 녹음 활성화 상태
    var isRecordingEnabled by mutableStateOf(true)
        private set

    // 녹음 목록 (StateFlow)
    private val _recordings = MutableStateFlow<List<Recording>>(emptyList())
    val recordings: StateFlow<List<Recording>> = _recordings.asStateFlow()

    init {
        loadRecordings()
    }

    fun toggleRecording(enabled: Boolean) {
        isRecordingEnabled = enabled
        // TODO: 서비스에 상태 전달 등
    }

    fun loadRecordings() {
        viewModelScope.launch {
            repository.autoDeleteOldRecordings()
            _recordings.value = repository.getAllRecordings()
        }
    }

    fun deleteRecording(recording: Recording) {
        viewModelScope.launch {
            repository.deleteRecording(recording)
            loadRecordings()
        }
    }

    fun shareRecording(recording: Recording): Intent {
        return repository.shareRecording(recording)
    }
} 