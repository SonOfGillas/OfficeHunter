package com.officehunter.ui.screens.hunterInfo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HunterInfoScreen(
    state: HunterInfoState,
    actions: HunterInfoActions,
    navController: NavHostController
){
    when(state.hunterInfoStep){
        HunterInfoStep.QUESTIONS_PAGE_1 -> QuestionsScreen(state = state, actions = actions )
        else -> QuestionsScreen(state = state, actions = actions )

    }
}