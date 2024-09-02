package com.officehunter.ui.screens.hunt

import androidx.compose.runtime.Composable
import com.officehunter.R
import com.officehunter.ui.composables.AchievementDialog
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo

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

    val achievementToShow = if (state.achievementsToShow.isNotEmpty()) state.achievementsToShow[0] else null
    AchievementDialog(achievementToShow,actions::getAchievementsIcon, actions::closeAchievement)
}



