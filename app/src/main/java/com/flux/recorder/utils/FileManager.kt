package com.flux.recorder.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val context: Context) {

    companion object {
        const val DEFAULT_STORAGE_PATH = "/storage/emulated/0/DCIM/screenrecorder"
        private const val FILE_PREFIX = "Screenrecorder-"
        private const val FILE_EXTENSION = ".mp4"
    }

    private val prefsManager = PreferencesManager(context)

    fun getRecordingsDirectory(): File {
        val path = prefsManager.getStoragePath()
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun createRecordingFile(): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(Date())
        val fileName = "$FILE_PREFIX$timestamp$FILE_EXTENSION"
        return File(getRecordingsDirectory(), fileName)
    }

    fun getAllRecordings(): List<File> {
        val dir = getRecordingsDirectory()
        if (!dir.exists()) return emptyList()
        return dir.listFiles { file ->
            file.isFile && file.extension == "mp4"
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    fun deleteRecording(file: File): Boolean {
        return file.delete()
    }

    fun getAvailableSpace(): Long {
        val dir = getRecordingsDirectory()
        return dir.usableSpace
    }

    fun hasEnoughSpace(estimatedDurationMinutes: Int, bitrate: Int): Boolean {
        val estimatedSizeBytes = (estimatedDurationMinutes * 60L * bitrate) / 8
        val requiredSpace = (estimatedSizeBytes * 1.2).toLong()
        return getAvailableSpace() > requiredSpace
    }
}
