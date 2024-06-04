package com.example.composecamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.composecamera.ui.components.CameraPreview
import com.example.composecamera.ui.theme.ComposeCameraTheme

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
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {}
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
                            modifier = Modifier.align(Alignment.TopEnd).safeDrawingPadding()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Switch Camera"
                            )
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

private fun isPermissionGranted(context: Context) = MainActivity.CAMERA_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
        context,
        it
    ) == PackageManager.PERMISSION_GRANTED
}

