package org.foi.rampu.geogallery

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.foi.rampu.geogallery.databinding.ActivityAudioBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAudioBinding

    private var audioCapture: MediaRecorder? = null
    private var isRecording: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.ibtnRecord.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            } else {
                if (!isRecording) {
                    startRecording()
                } else {
                    stopRecording()
                }
            }
        }
    }

    private fun prepareRecorder() {
        audioCapture = MediaRecorder()
        audioCapture?.setAudioSource(MediaRecorder.AudioSource.MIC)
        audioCapture?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        audioCapture?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        audioCapture?.setAudioEncodingBitRate(16)
        audioCapture?.setAudioSamplingRate(44100)

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Audio.Media.RELATIVE_PATH, "Recordings")
            }
        }
        val audioUri =
            contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)
        val file = contentResolver.openFileDescriptor(audioUri!!, "w")

        audioCapture?.setOutputFile(file?.fileDescriptor)
        audioCapture?.prepare()
    }

    private fun startRecording() {
        prepareRecorder()
        isRecording = true

        try {
            audioCapture?.start()
        } catch (error: IOException) {
            error.printStackTrace()
        }

        viewBinding.ibtnRecord.setColorFilter(
            R.color.red,
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun stopRecording() {
        isRecording = false

        audioCapture?.stop()
        audioCapture?.reset()
        audioCapture?.release()
        audioCapture = null

        viewBinding.ibtnRecord.setColorFilter(
            R.color.black,
            PorterDuff.Mode.SRC_ATOP
        )
        Toast.makeText(baseContext, "Added recording", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}