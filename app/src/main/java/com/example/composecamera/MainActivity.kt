package com.example.composecamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecamera.ui.PhotoViewModel
import com.example.composecamera.ui.components.CameraPreview
import com.example.composecamera.ui.components.PhotoList
import com.example.composecamera.ui.theme.ComposeCameraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isPermissionGranted(this)){
            ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSIONS,
                0
            )
        }
        enableEdgeToEdge()
        setContent {
            ComposeCameraTheme {
                val scaffoldState = rememberBottomSheetScaffoldState()
                val context = LocalContext.current
                val cameraController = remember{
                    LifecycleCameraController(context).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE
                        )
                    }
                }
                val viewmodel: PhotoViewModel = viewModel()
                val state = viewmodel.state.collectAsState()
                val scope = rememberCoroutineScope()
                println("Number of photo: ${state.value.size}")

                BottomSheetScaffold(
                    modifier = Modifier.safeDrawingPadding(),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {
                        PhotoList(bitmaps = state.value, modifier = Modifier.safeDrawingPadding())
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ){
                        CameraPreview(
                            cameraController = cameraController,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                     cameraController.cameraSelector =
                                         if(cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                                             CameraSelector.DEFAULT_FRONT_CAMERA
                                         } else {
                                             CameraSelector.DEFAULT_BACK_CAMERA
                                         }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .safeDrawingPadding()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Switch Camera"
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .safeDrawingPadding()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            IconButton(onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Gallery"
                                )
                            }
                            IconButton(onClick = {
                                takePhoto(
                                    cameraController,
                                    context
                                ){ bitmap ->
                                    viewmodel.onTakePhoto(bitmap)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Take Photo"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object{
        val CAMERA_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}

private fun takePhoto(
    cameraController: LifecycleCameraController,
    context: Context,
    onPhotoTaken: (Bitmap) -> Unit,
){
    cameraController.takePicture(
        ContextCompat.getMainExecutor(context),
        object: OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                onPhotoTaken(image.toBitmap())
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("Photo error", "Could not take photo: ${exception.message}")
            }
        }
    )
}

private fun isPermissionGranted(context: Context) = MainActivity.CAMERA_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
        context,
        it
    ) == PackageManager.PERMISSION_GRANTED
}

