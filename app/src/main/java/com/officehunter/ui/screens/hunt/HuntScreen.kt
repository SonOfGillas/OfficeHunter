package com.officehunter.ui.screens.hunt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.officehunter.R
import com.officehunter.ui.composables.AchievementDialog
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.DefaultMapStartingPosition
import com.officehunter.ui.composables.map.MarkerInfo
import com.officehunter.utils.LocationService
import com.officehunter.utils.PermissionStatus
import com.officehunter.utils.rememberPermission
import org.koin.compose.koinInject
import org.osmdroid.util.GeoPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HuntScreen(
    state: HuntState,
    actions: HuntActions
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val locationService = koinInject<LocationService>()
    val ctx = LocalContext.current

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                locationService.requestCurrentLocation()

            PermissionStatus.Denied ->
                actions.setShowLocationPermissionDeniedAlert(true)

            PermissionStatus.PermanentlyDenied ->
                actions.setShowLocationPermissionPermanentlyDeniedSnackbar(true)

            PermissionStatus.Unknown -> {}
        }
    }
    fun requestLocation() {
        Log.d("HuntScreen","requestLocation")
        if (locationPermission.status.isGranted) {
            Log.d("HuntScreen","requestLocation isGranted")
            locationService.requestCurrentLocation()
        } else {
            Log.d("HuntScreen","requestLocation launchPermissionRequest")
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationService.coordinates) {
        if (locationService.coordinates == null) return@LaunchedEffect
        Log.d("HuntScreen","coordinates ${locationService.coordinates}")
        if(locationService.coordinates != null){
            actions.setUserPosition(locationService.coordinates!!)
            actions.startSpawningHunted()
        }
    }

    val localLifecycle = LocalLifecycleOwner.current
    DisposableEffect(localLifecycle){
        val observer = LifecycleEventObserver{
            _,event ->
            if (event == Lifecycle.Event.ON_CREATE){
                Log.d("HuntScreen", "onCreate")
                requestLocation()
            }
        }

        localLifecycle.lifecycle.addObserver(observer)
        onDispose {
            localLifecycle.lifecycle.removeObserver(observer)
            actions.stopSpawning()
        }
    }



    LaunchedEffect(locationService.isLocationEnabled) {
        Log.d("locationPermission","permisison Denied")
        //actions.setShowLocationDisabledAlert(locationService.isLocationEnabled == false)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState){ data ->
            Snackbar(snackbarData = data, containerColor = MaterialTheme.colorScheme.error)
            }
        },
    ){
        if(state.status == HuntStatus.SETUP){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color =  MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            AppMap(
                markersInfo = state.markerInfos, startingZoom = 18.0,
                startingPosition = state.userPosition ?: DefaultMapStartingPosition
            )
        }

        if (state.hasError()) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(
                    message = "${state.errorMessage}",
                    duration = SnackbarDuration.Short
                )
                actions.resetError()
            }
        }
    }

    HuntDialog(state,actions)

    val achievementToShow = if (state.achievementsToShow.isNotEmpty()) state.achievementsToShow[0] else null
    AchievementDialog(achievementToShow,actions::getAchievementsIcon, actions::closeAchievement)

    if (state.showLocationDisabledAlert) {
        AlertDialog(
            title = { Text("Location disabled") },
            text = { Text("Location must be enabled to get your current location in the app.") },
            confirmButton = {
                TextButton(onClick = {
                    locationService.openLocationSettings()
                    actions.setShowLocationDisabledAlert(false)
                }) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { actions.setShowLocationDisabledAlert(false) }) {
                    Text("Dismiss")
                }
            },
            onDismissRequest = { actions.setShowLocationDisabledAlert(false) }
        )
    }

    if (state.showLocationPermissionDeniedAlert) {
        AlertDialog(
            title = { Text("Location permission denied") },
            text = { Text("Location permission is required to get your current location in the app.") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermission.launchPermissionRequest()
                    actions.setShowLocationPermissionDeniedAlert(false)
                }) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = { actions.setShowLocationPermissionDeniedAlert(false) }) {
                    Text("Dismiss")
                }
            },
            onDismissRequest = { actions.setShowLocationPermissionDeniedAlert(false) }
        )
    }

    if (state.showLocationPermissionPermanentlyDeniedSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                "Location permission is required.",
                "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                ctx.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            actions.setShowLocationPermissionPermanentlyDeniedSnackbar(false)
        }
    }

}



