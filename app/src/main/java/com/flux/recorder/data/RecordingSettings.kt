package com.flux.recorder.data

import android.os.Parcelable
import androidx.annotation.StringRes
import com.flux.recorder.R
import kotlinx.parcelize.Parcelize

/**
 * Video quality/resolution options
 */
enum class VideoQuality(val width: Int, val height: Int, @StringRes val labelResId: Int) {
    QUALITY_360P(640, 360, R.string.quality_360p),
    QUALITY_480P(854, 480, R.string.quality_480p),
    QUALITY_720P(1280, 720, R.string.quality_720p),
    QUALITY_1080P(1920, 1080, R.string.quality_1080p),
    QUALITY_2K(2560, 1440, R.string.quality_2k),
    QUALITY_4K(3840, 2160, R.string.quality_4k)
}

/**
 * Frame rate options
 */
enum class FrameRate(val fps: Int, @StringRes val labelResId: Int) {
    FPS_30(30, R.string.fps_30),
    FPS_60(60, R.string.fps_60),
    FPS_90(90, R.string.fps_90)
}

/**
 * Audio source configuration
 */
enum class AudioSource(@StringRes val labelResId: Int) {
    NONE(R.string.audio_none),
    INTERNAL(R.string.audio_internal),
    MICROPHONE(R.string.audio_microphone),
    BOTH(R.string.audio_both)
}

/**
 * Recording configuration settings
 */
@Parcelize
data class RecordingSettings(
    val videoQuality: VideoQuality = VideoQuality.QUALITY_720P,
    val frameRate: FrameRate = FrameRate.FPS_30,
    val audioSource: AudioSource = AudioSource.BOTH,
    val enableFacecam: Boolean = false,
    val enableShakeToStop: Boolean = true,
    val shakeSensitivity: Float = 2.5f // Acceleration threshold in m/s²
) : Parcelable {
    /**
     * Calculate optimal bitrate based on resolution and frame rate
     * Formula: width * height * fps * motion_factor * 0.07
     */
    /**
     * Calculate optimal bitrate based on resolution and frame rate
     * Formula: width * height * fps * motion_factor * 0.12 (Balanced Quality/Size)
     */
    fun calculateBitrate(): Int {
        val pixels = videoQuality.width * videoQuality.height
        val motionFactor = 1.5f // Assume medium motion
        val qualityFactor = 0.12f // Balanced for VBR (High Quality, smaller size)
        return (pixels * frameRate.fps * motionFactor * qualityFactor).toInt()
    }
}
