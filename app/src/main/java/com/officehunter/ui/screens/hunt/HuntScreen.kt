package com.officehunter.ui.screens.hunt

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.officehunter.R
import com.officehunter.ui.composables.AchievementDialog
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo
import org.osmdroid.util.GeoPoint

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HuntScreen(
    state: HuntState,
    actions: HuntActions
) {
    Log.d("HuntScreen","Status ${state.status} ")

/*
    val markerInfos =
        state.spawnedHunted.map{
        MarkerInfo(
            it.position,
            icon = R.drawable.logov2_shadow,
            onClick = {actions.hunt(it.hunted)}
        )
    }

 */


    val localLifecycle = LocalLifecycleOwner.current
    DisposableEffect(localLifecycle){
        val observer = LifecycleEventObserver{
            _,event ->
            if (event == Lifecycle.Event.ON_CREATE){
                Log.d("HuntScreen", "onCreate")
                actions.startSpawningHunted()
            }
        }

        localLifecycle.lifecycle.addObserver(observer)
        onDispose {
            localLifecycle.lifecycle.removeObserver(observer)
            actions.stopSpawning()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

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
                markersInfo = state.markerInfos, startingZoom = 18.0
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
}



