package com.akopyan757.yandexvoicesample

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException

class SpeechRecognitionWrapper(
    private val outputFilename: String
) {

    private var recorder: MediaRecorder? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    fun startSpeech() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFile(outputFilename)
            setMaxDuration(MAX_DURATION)
            setMaxFileSize(MAX_FILE_SIZE)
            setOutputFormat(MediaRecorder.OutputFormat.OGG)
            setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
            prepare()
            start()
        }
    }

    fun stopSpeech() {
        recorder?.release()
        recorder = null
    }

    companion object {
        private const val MAX_DURATION = 15000
        private const val MAX_FILE_SIZE = 1024 * 1024L
    }
}