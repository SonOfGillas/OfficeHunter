package com.officehunter.ui.screens.hunterInfo

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.composables.AppButton
import com.officehunter.utils.rememberCameraLauncher
import com.officehunter.utils.rememberPermission

@Composable
fun HuntedAvatar(
    state: HunterInfoState,
    actions: HunterInfoActions,
){
    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    Log.d("Question Screen","${state.hireDate} ${state.workRole} ${state.hireDate}")

    // Camera
    val cameraLauncher = rememberCameraLauncher { imageUri ->
        actions.setAvatarImageUri(imageUri)
    }

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState){ data ->
            Snackbar(snackbarData = data, containerColor = MaterialTheme.colorScheme.error)
        } },
    ){ contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ){
            val image = painterResource(R.drawable.logov2)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.width(160.dp)
            )
            Text(
                "Hunter Avatar",
                fontSize = 20.sp,
                color =  MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.size(24.dp))
            Box(modifier = Modifier.width(260.dp)){
                Text(
                    "Upload a photo for the avatar of your hunted",
                    fontSize = 16.sp,
                    color =  MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier.weight(1.0f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if(state.avatarImageUri != Uri.EMPTY){
                    val imageSize = 300
                    val boarderRadius = (imageSize/2).dp
                    Box(modifier = Modifier
                        .width(imageSize.dp)
                        .height(imageSize.dp)
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.onBackground), RoundedCornerShape(boarderRadius),)
                        .clip(RoundedCornerShape(boarderRadius))
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(state.avatarImageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                AppButton(
                    label = "Take a Photo",
                    onClick = {takePicture()},
                    icon = Icons.Filled.CameraAlt,
                    fillMaxWidth = false)
            }
            Spacer(modifier = Modifier.size(24.dp))
            AppButton(label = "Finish",onClick = {actions.onFinish()})
            Spacer(modifier = Modifier.size(24.dp))

            if (state.hasError()) {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${state.errorMessage}",
                        duration = SnackbarDuration.Short
                    )
                    actions.closeError()
                }
            }
        }
    }
}