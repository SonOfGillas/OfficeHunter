package com.officehunter.ui.screens.hunt

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.officehunter.R
import com.officehunter.data.repositories.defaultAchievements
import com.officehunter.ui.composables.AchievementDialog
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo
import com.officehunter.ui.screens.hunted.HuntedDetailDialog
import org.osmdroid.util.GeoPoint
import kotlin.random.Random

@Composable
fun HuntScreen(
    state: HuntState,
    actions: HuntActions
) {
    val markerInfos = state.spawnedHunted.map{
        MarkerInfo(
            it.position,
            icon = R.drawable.logov2_shadow,
            onClick = {actions.hunt(it.hunted)}
        )
    }

    AppMap(
       markersInfo = markerInfos, startingZoom = 18.0
    )

    HuntDialog(state,actions)

    AchievementDialog(defaultAchievements[0],actions::getAchievementsIcon){}
}



