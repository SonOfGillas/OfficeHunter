package com.officehunter.ui.screens.hunt

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.officehunter.R
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo
import org.osmdroid.util.GeoPoint
import kotlin.random.Random

@Composable
fun HuntScreen(
    state: HuntState,
    actions: HuntActions
) {
    val markerInfos = state.spawnedHunted.map{
        MarkerInfo(
            it.position
        )
    }

    AppMap(
       markersInfo = markerInfos, startingZoom = 18.0
    )
}



