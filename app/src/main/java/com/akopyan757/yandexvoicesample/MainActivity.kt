package com.akopyan757.yandexvoicesample

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnTouchListener, UploadAudioService.RecognizeAudioListener {

    private lateinit var tvResult: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnRecord: Button

    private lateinit var speechWrapper: SpeechRecognitionWrapper
    private lateinit var audioService: UploadAudioService
    private lateinit var audioFilename: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnRecord.setOnTouchListener(this)
        btnRecord.isEnabled = false
        tvResult = findViewById(R.id.tvSpeechResult)
        tvStatus = findViewById(R.id.tvSpeechStatus)
        tvStatus.text = getString(R.string.button_for_record)

        audioFilename = getAudioOutputFilename()
        speechWrapper = SpeechRecognitionWrapper(getAudioOutputFilename())
        audioService = UploadAudioService()

        if (checkAudioPermission().not()) {
            requestAudioPermission()
        } else {
            btnRecord.isEnabled = true
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> { onActionDown(); true }
            MotionEvent.ACTION_UP -> { onActionUp(); false }
            else -> super.onTouchEvent(event)
        }
    }

    private fun onActionDown() {
        tvResult.text = EMPTY_STRING
        if (isQVersion()) {
            tvStatus.text = getString(R.string.record_speech)
            speechWrapper.startSpeech()
        } else {
            tvStatus.text = getString(R.string.error_support)
            showToast(R.string.error_support)
        }
    }

    private fun onActionUp() {
        speechWrapper.stopSpeech()
        audioService.recognizeAudio(audioFilename, this)
        tvStatus.text = getString(R.string.recognize_text)
    }

    override fun onSuccessRecognize(result: String) {
        tvResult.text = result
        tvStatus.text = getString(R.string.button_for_record)
    }

    override fun onErrorRecognize(throwable: Throwable) {
        tvResult.text = EMPTY_STRING
        tvStatus.text = getString(R.string.button_for_record)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isAudioRequestCode(requestCode)) {
            if (isPermissionGranted(grantResults)) {
                btnRecord.isEnabled = true
                showToast(R.string.granted_permission)
            } else {
                showToast(R.string.error_permission)
                tvStatus.text = getString(R.string.error_permission)
            }
        }
    }

    private fun checkAudioPermission(): Boolean {
        val code = ContextCompat.checkSelfPermission(this, PERMISSION)
        return code == PackageManager.PERMISSION_GRANTED
    }

    private fun isAudioRequestCode(requestCode: Int): Boolean {
        return requestCode == AUDIO_PERMISSION_CODE
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(PERMISSION), AUDIO_PERMISSION_CODE)
    }

    private fun showToast(@StringRes messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_LONG).show()
    }

    private fun isQVersion() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    private fun getAudioOutputFilename(): String {
        val cacheDirPath = applicationContext.externalCacheDir!!.absolutePath
        return "$cacheDirPath/output.ogg"
    }

    companion object {
        private const val PERMISSION = android.Manifest.permission.RECORD_AUDIO
        private const val AUDIO_PERMISSION_CODE = 123

        private const val EMPTY_STRING = ""
    }
}