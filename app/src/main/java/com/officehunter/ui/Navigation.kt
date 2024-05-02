package com.officehunter.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.officehunter.ui.screens.addtravel.AddTravelScreen
import com.officehunter.ui.screens.addtravel.AddTravelViewModel
import com.officehunter.ui.screens.settings.SettingsScreen
import com.officehunter.ui.screens.settings.SettingsViewModel
import com.officehunter.ui.screens.traveldetails.TravelDetailsScreen
import com.officehunter.ui.screens.home.HomeScreen
import com.officehunter.ui.screens.login.LoginScreen
import com.officehunter.ui.screens.login.LoginViewModel
import org.koin.androidx.compose.koinViewModel

sealed class OfficeHunterRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : OfficeHunterRoute("travels", "TravelDiary")
    data object TravelDetails : OfficeHunterRoute(
        "travels/{travelId}",
        "Travel Details",
        listOf(navArgument("travelId") { type = NavType.StringType })
    ) {
        fun buildRoute(travelId: String) = "travels/$travelId"
    }
    data object AddTravel : OfficeHunterRoute("travels/add", "Add Travel")
    data object Settings : OfficeHunterRoute("settings", "Settings")
    data object Login: OfficeHunterRoute("login","Login")

    companion object {
        val routes = setOf(Home, TravelDetails, AddTravel, Settings)
    }
}

@Composable
fun OfficeHunterNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val placesVm = koinViewModel<PlacesViewModel>()
    val placesState by placesVm.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = OfficeHunterRoute.Home.route,
        modifier = modifier
    ) {
        with(OfficeHunterRoute.Home) {
            composable(route) {
                HomeScreen(placesState, navController)
            }
        }
        with(OfficeHunterRoute.TravelDetails) {
            composable(route, arguments) { backStackEntry ->
                val place = requireNotNull(placesState.places.find {
                    it.id == backStackEntry.arguments?.getString("travelId")?.toInt()
                })
                TravelDetailsScreen(place)
            }
        }
        with(OfficeHunterRoute.AddTravel) {
            composable(route) {
                val addTravelVm = koinViewModel<AddTravelViewModel>()
                val state by addTravelVm.state.collectAsStateWithLifecycle()
                AddTravelScreen(
                    state = state,
                    actions = addTravelVm.actions,
                    onSubmit = { placesVm.addPlace(state.toPlace()) },
                    navController = navController
                )
            }
        }
        with(OfficeHunterRoute.Settings) {
            composable(route) {
                val settingsVm = koinViewModel<SettingsViewModel>()
                SettingsScreen(settingsVm.state, settingsVm::setUsername)
            }
        }
        with(OfficeHunterRoute.Login){
            composable(route){
                val loginVm = koinViewModel<LoginViewModel>()
                LoginScreen(loginVm.state, loginVm::setEmail, loginVm::setPassword )
            }
        }
    }
}
