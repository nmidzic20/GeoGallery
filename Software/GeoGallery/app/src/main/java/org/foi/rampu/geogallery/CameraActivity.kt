package org.foi.rampu.geogallery

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.Exif
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.foi.rampu.geogallery.classes.AllLocationsInfo
import org.foi.rampu.geogallery.classes.CurrentLocationInfo
import org.foi.rampu.geogallery.classes.LocationTest
import org.foi.rampu.geogallery.classes.SavedLocationInfo
import org.foi.rampu.geogallery.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val location = LocationTest(this)

    var currentUri : Uri = Uri.EMPTY


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewBinding.ibtnPhoto.setOnClickListener { takePhoto() }
        viewBinding.ibtnVideo.setOnClickListener { captureVideo() }
        viewBinding.ibtnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        CurrentLocationInfo.locationInfo.observe(this, Observer {
            Log.i("ADDRESS LOCATION INFO MUTABLE DATA", it.toString())
            //create a new element and add to alllocationsinfo, then fetch the last element from it
            //if exists, return that element
            //and add to the taken photo/video's metadata
            AllLocationsInfo.savedLocationInfo.add(
                SavedLocationInfo(
                    CurrentLocationInfo.locationInfo.value?.get("country").toString(),
                    CurrentLocationInfo.locationInfo.value?.get("city").toString(),
                    CurrentLocationInfo.locationInfo.value?.get("street").toString(),
                    currentUri
                )
            )
            Log.i("ADDRESS LOCATION INFO SAVED", AllLocationsInfo.savedLocationInfo.get(
                AllLocationsInfo.savedLocationInfo.lastIndex
            ).toString())

            if (currentUri != Uri.EMPTY)
            {
                var data = AllLocationsInfo.savedLocationInfo.get(
                        AllLocationsInfo.savedLocationInfo.lastIndex
                    ).country + AllLocationsInfo.savedLocationInfo.get(
                        AllLocationsInfo.savedLocationInfo.lastIndex
                    ).city + AllLocationsInfo.savedLocationInfo.get(
                        AllLocationsInfo.savedLocationInfo.lastIndex
                    ).street

                var exifData = ExifInterface(this.contentResolver.openFileDescriptor(currentUri, "rw", null)!!.fileDescriptor)
                exifData.setAttribute("UserComment", data)
                exifData.saveAttributes()

                Log.i("ADDRESS EXIF 1", getTagString("UserComment", exifData).toString())

            }

        }
        )
    }

    private fun getTagString(tag: String, exif: ExifInterface): String?
    {
        return """$tag : ${exif.getAttribute(tag)}"""
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults)
                {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    saveLocation(output.savedUri!!)
                }
            }
        )
    }

    private fun saveLocation(uri : Uri) {
        Log.i("ADDRESS SAVE LOCATION", "came here")
        currentUri = uri
        location.countryName(fusedLocationProviderClient)
        location.cityName(fusedLocationProviderClient)
        location.streetName(fusedLocationProviderClient)
        //metapodaci u sliku iz lastlocationfino zadnjeg elementa?

    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        viewBinding.ibtnVideo.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        viewBinding.ibtnVideo.apply {
                            viewBinding.ibtnVideo.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);//line changed
                            isEnabled = true
                        }

                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        viewBinding.ibtnVideo.apply {
                            viewBinding.ibtnVideo.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);//line changed
                            isEnabled = true
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}