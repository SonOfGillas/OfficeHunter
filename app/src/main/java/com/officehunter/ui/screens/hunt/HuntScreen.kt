package com.officehunter.ui.screens.hunt

import android.util.Log
import androidx.compose.runtime.Composable
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
    /*
val markerInfos =
listOf<MarkerInfo>(

MarkerInfo(
    GeoPoint(44.495083, 11.832050), icon = R.drawable.points
)
    )
     */

    /* TODO add the check that the use is in an office */
    val officePoint = GeoPoint(44.495083, 11.832050)

    /*
    val markerInfos = state.spawnedHunted.map {hunted ->
        val latitude = 44.495 + (Random.nextInt() * 0.0001)
        val longitude = 11.832 + (Random.nextInt() * 0.0001)
        MarkerInfo(
            GeoPoint(latitude, longitude)
        )
    }
     */

    AppMap(
       // markersInfo = markerInfos
    )
}



