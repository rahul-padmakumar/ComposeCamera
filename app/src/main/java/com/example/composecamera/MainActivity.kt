package com.example.composecamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.remember
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
                    CameraPreview(
                        cameraController = cameraController,
                        modifier = Modifier.fillMaxSize()
                    )
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

