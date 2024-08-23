package com.officehunter.ui.screens.hunt

import androidx.compose.runtime.Composable
import com.officehunter.R
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo
import org.osmdroid.util.GeoPoint

@Composable
fun HuntScreen(
    actions: HuntActions
) {
    val markerInfos = listOf<MarkerInfo>(
        MarkerInfo(
            GeoPoint(44.495083, 11.832050), icon = R.drawable.points
        )
    )

    AppMap(
        markersInfo = markerInfos
    )
}



