package com.officehunter.ui.screens.hunterInfo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.officehunter.ui.OfficeHunterRoute

@Composable
fun HunterInfoScreen(
    state: HunterInfoState,
    actions: HunterInfoActions,
    navController: NavHostController
){
    if (state.hunterInfoPhase == HunterInfoPhase.COMPLETED){
        navController.navigate(OfficeHunterRoute.Hunt.route)
    }

    when(state.hunterInfoStep){
        HunterInfoStep.QUESTIONS_PAGE_1 -> QuestionsScreen(state = state, actions = actions )
        HunterInfoStep.HUNTED_AVATAR -> HuntedAvatar(state = state, actions = actions)
        HunterInfoStep.QUESTIONS_ENDED -> navController.navigate(OfficeHunterRoute.Hunt.route)
        else -> QuestionsScreen(state = state, actions = actions )
    }
}