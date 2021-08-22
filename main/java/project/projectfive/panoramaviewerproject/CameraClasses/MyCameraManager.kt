package project.projectfive.panoramaviewerproject.CameraClasses

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import project.projectfive.panoramaviewerproject.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyCameraManager(context: AppCompatActivity, cameraButton:Button, previewView: PreviewView) {
    private var imageCapture: ImageCapture? = null

    private var outputDirectory: File
    private var cameraExecutor: ExecutorService

    init{
        if (allPermissionsGranted(context)) {
            startCamera(context, previewView)
        } else {
            ActivityCompat.requestPermissions(
                context, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        cameraButton.setOnClickListener { takePhoto(context) }
        outputDirectory = getOutputDirectory(context)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun tryToStartCamera(context: AppCompatActivity, requestCode:Int, previewView: PreviewView){
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(context)) {
                Toast.makeText(
                    context,
                    "Permissions GRANTED",
                    Toast.LENGTH_SHORT
                ).show()
                startCamera(context, previewView)
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                context.finish()
            }
        }
    }

    private fun takePhoto(context: AppCompatActivity) {
        val a = 1

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(context.baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun startCamera(context: AppCompatActivity, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    context, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    private fun allPermissionsGranted(context: AppCompatActivity) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(context: AppCompatActivity): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir
    }

    fun takeOff(){
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}