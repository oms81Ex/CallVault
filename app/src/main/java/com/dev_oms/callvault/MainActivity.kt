package com.dev_oms.callvault

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev_oms.callvault.ui.theme.CallVaultTheme
import kotlinx.coroutines.launch
import com.dev_oms.callvault.Recording
// Compose UI utilities
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.media.MediaPlayer
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import com.dev_oms.callvault.ui.DetailScreen
import androidx.compose.foundation.clickable
import com.dev_oms.callvault.ui.SettingsScreen
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(applicationContext) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CallVaultTheme {
                MainScreen(mainViewModel, onShare = { intent ->
                    startActivity(Intent.createChooser(intent, getString(R.string.share)))
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, onShare: (Intent) -> Unit) {
    val isRecordingEnabled = viewModel.isRecordingEnabled
    val recordings by viewModel.recordings.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val (playingFile, setPlayingFile) = remember { mutableStateOf<String?>(null) }
    val (isPlaying, setIsPlaying) = remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }
    val (detailTarget, setDetailTarget) = remember { mutableStateOf<Recording?>(null) }
    val (showSettings, setShowSettings) = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { setShowSettings(true) }) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.settings))
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRecordingEnabled) stringResource(id = R.string.recording_on) else stringResource(id = R.string.recording_off),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isRecordingEnabled,
                        onCheckedChange = { viewModel.toggleRecording(it) }
                    )
                }
            }
        },
        content = { innerPadding ->
            if (recordings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.no_recordings))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(recordings) { recording ->
                        RecordingItem(
                            recording = recording,
                            isPlaying = isPlaying && playingFile == recording.filePath,
                            onPlay = {
                                if (isPlaying && playingFile == recording.filePath) {
                                    mediaPlayer.stop()
                                    mediaPlayer.reset()
                                    setIsPlaying(false)
                                    setPlayingFile(null)
                                } else {
                                    try {
                                        mediaPlayer.reset()
                                        mediaPlayer.setDataSource(recording.filePath)
                                        mediaPlayer.prepare()
                                        mediaPlayer.start()
                                        setPlayingFile(recording.filePath)
                                        setIsPlaying(true)
                                        mediaPlayer.setOnCompletionListener {
                                            setIsPlaying(false)
                                            setPlayingFile(null)
                                        }
                                    } catch (e: Exception) {
                                        setIsPlaying(false)
                                        setPlayingFile(null)
                                    }
                                }
                            },
                            onShare = {
                                val intent = viewModel.shareRecording(recording)
                                onShare(intent)
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    viewModel.deleteRecording(recording)
                                }
                            },
                            onClick = { setDetailTarget(recording) }
                        )
                    }
                }
            }
            detailTarget?.let { rec ->
                DetailScreen(recording = rec, onClose = { setDetailTarget(null) })
            }
            
            if (showSettings) {
                SettingsScreen(
                    onBack = { setShowSettings(false) },
                    context = context
                )
            }
        }
    )
}

@Composable
fun RecordingItem(
    recording: Recording,
    isPlaying: Boolean = false,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recording.fileName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = recording.date,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = recording.duration,
                    fontSize = 14.sp
                )
                IconButton(onClick = onPlay) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(id = R.string.pause) else stringResource(id = R.string.play)
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = stringResource(id = R.string.share))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete))
                }
            }
        }
    }
}