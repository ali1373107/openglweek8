package com.example.openglweek8

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin


class MainActivity : AppCompatActivity() {
    var cameraFeedSurfaceTexure: SurfaceTexture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_main)

        val glView = OpenGLView(this) {cameraFeedSurfaceTexure = it
            checkPermissions()
        }
        setContentView(glView)

//we dont need the below code for exercise week 12 and either for assigment
        /* findViewById<Button>(R.id.minusX).setOnClickListener{
             glView.camera.translate(-1f,0f,0f)
         }
         findViewById<Button>(R.id.plusX).setOnClickListener{
             glView.camera.translate(1f,0f,0f)
         }
         findViewById<Button>(R.id.minusY).setOnClickListener{
             glView.camera.translate(0f,-1f,0f)
         }
         findViewById<Button>(R.id.plusY).setOnClickListener{
             glView.camera.translate(0f,1f,0f)
         }
         findViewById<Button>(R.id.minusZ).setOnClickListener{
             glView.camera.translate(0f,0f,-1f)
         }
         findViewById<Button>(R.id.plusZ).setOnClickListener{
             glView.camera.translate(0f,0f,1f)
         }
         findViewById<Button>(R.id.NegXCloWise).setOnClickListener{
             glView.camera.rotate(-10f)
         }
         findViewById<Button>(R.id.PlXuntiClowise).setOnClickListener{
             glView.camera.rotate(10f)
         }
         findViewById<Button>(R.id.forward).setOnClickListener{
             val d = 1
             val radion =glView.camera.rotation * (Math.PI/180f)
             val  XDirection = d * sin(radion).toFloat()
             val  ZDirection = d * cos(radion).toFloat()
             glView.camera.translate(-XDirection,0f,-ZDirection)
         }
         findViewById<Button>(R.id.backward).setOnClickListener{
             val d = 1
             val radion =glView.camera.rotation * (Math.PI/180f)
             val  XDirection = d * sin(radion).toFloat()
             val  ZDirection = d * cos(radion).toFloat()
             glView.camera.translate(XDirection,0f,ZDirection)
         }*/

    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            // Handle the case when camera permission is already granted
            startCamera()

        }
    }

    private fun startCamera(){

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {

                val surfaceProvider: (SurfaceRequest) -> Unit = { request ->
                    val resolution = request.resolution
                    cameraFeedSurfaceTexure?.apply {
                        setDefaultBufferSize(resolution.width, resolution.height)
                        val surface = Surface(this)
                        request.provideSurface(
                            surface,
                            ContextCompat.getMainExecutor(this@MainActivity.baseContext)
                        )
                        { }

                    }
                }
                it.setSurfaceProvider(surfaceProvider)

            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            } catch (e: Exception) {
                Log.e("OpenGL01Log", e.stackTraceToString())
            }
        }, ContextCompat.getMainExecutor(this))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Handle the case when camera permission is granted
            // Set "cameraPermission" to true in the lifecycle observer
            startCamera()
        } else {
            if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.CAMERA) {
                // Handle the case when camera permission is denied
                AlertDialog.Builder(this)
                    .setPositiveButton("OK", null)
                    .setMessage("Camera permission denied")
                    .show()
            }
        }
    }
}