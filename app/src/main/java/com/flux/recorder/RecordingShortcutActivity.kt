package com.flux.recorder

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.flux.recorder.data.RecordingState
import com.flux.recorder.service.RecorderService
import com.flux.recorder.utils.PreferencesManager

/**
 * Transparent activity that handles MediaProjection consent and starts recording immediately.
 * Used by Quick Settings Tile to avoid showing the full main UI.
 */
class RecordingShortcutActivity : ComponentActivity() {

    companion object {
        private const val TAG = "RecordingShortcut"
    }

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val settings = PreferencesManager(this).getRecordingSettings()
            val intent = Intent(this, RecorderService::class.java).apply {
                action = RecorderService.ACTION_START_RECORDING
                putExtra(RecorderService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(RecorderService.EXTRA_RESULT_DATA, result.data)
                putExtra(RecorderService.EXTRA_SETTINGS, settings)
            }
            startService(intent)
        } else {
            Log.w(TAG, "MediaProjection permission denied")
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (RecorderService.recordingState.value !is RecordingState.Idle) {
            // Already recording, just finish
            finish()
            return
        }

        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(manager.createScreenCaptureIntent())
    }
}
