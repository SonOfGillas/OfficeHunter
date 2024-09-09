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
import com.officehunter.ui.screens.hunt.HuntScreen
import com.officehunter.ui.screens.hunt.HuntViewModel
import com.officehunter.ui.screens.hunted.HuntedScreen
import com.officehunter.ui.screens.hunted.HuntedViewModel
import com.officehunter.ui.screens.hunterInfo.HunterInfoScreen
import com.officehunter.ui.screens.login.LoginScreen
import com.officehunter.ui.screens.login.LoginViewModel
import com.officehunter.ui.screens.offices.OfficesScreen
import com.officehunter.ui.screens.offices.OfficesViewModel
import com.officehunter.ui.screens.profile.ProfileScreen
import com.officehunter.ui.screens.profile.ProfileViewModel
import com.officehunter.ui.screens.hunterInfo.QuestionsScreen
import com.officehunter.ui.screens.hunterInfo.HunterInfoViewModel
import com.officehunter.ui.screens.signUp.SignUpScreen
import com.officehunter.ui.screens.signUp.SignUpViewModel
import com.officehunter.ui.screens.splash.SplashScreen
import com.officehunter.ui.screens.splash.SplashViewModel
import com.officehunter.ui.screens.stats.StatsScreen
import com.officehunter.ui.screens.stats.StatsViewModel
import org.koin.androidx.compose.koinViewModel

sealed class OfficeHunterRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Splash: OfficeHunterRoute("Splash","Splash")
    data object Login : OfficeHunterRoute("login","Login")
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
    data object  SignUp : OfficeHunterRoute("signup","Sign Up")
    data object HunterInfo : OfficeHunterRoute("hunterInfo", "Hunter Info")
    data object Offices: OfficeHunterRoute("offices","Offices")
    data object Hunted: OfficeHunterRoute("hunted","Hunted")
    data object Hunt: OfficeHunterRoute("hunt","Hunt")
    data object Stats: OfficeHunterRoute("stats","Stats")
    data object Profile : OfficeHunterRoute("profile", "Profile")

    companion object {
        val routes = setOf(Splash, Home, TravelDetails, AddTravel, Settings,Login,SignUp, HunterInfo, Offices, Hunted, Hunt, Stats, Profile)
    }
}

@Composable
fun OfficeHunterNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val placesVm = koinViewModel<PlacesViewModel>()
    val placesState by placesVm.state.collectAsStateWithLifecycle()
    val huntedVm = koinViewModel<HuntedViewModel>()
    val filteredHuntedData by huntedVm.filteredHuntedData.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = OfficeHunterRoute.Splash.route,
        modifier = modifier
    ) {
        with(OfficeHunterRoute.Splash){
            composable(route){
                val splashVm = koinViewModel<SplashViewModel>()
                val state by splashVm.state.collectAsStateWithLifecycle()
                SplashScreen(state,navController)
            }
        }
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
                LoginScreen(loginVm.state, loginVm.actions, navController = navController )
            }
        }
        with(OfficeHunterRoute.SignUp){
            composable(route){
                val signUpVm = koinViewModel<SignUpViewModel>()
                val state by signUpVm.state.collectAsStateWithLifecycle()
                SignUpScreen(
                    state = state,
                    actions = signUpVm.actions,
                    navController = navController )
            }
        }
        with(OfficeHunterRoute.HunterInfo){
            composable(route){
                val hunterInfoVm = koinViewModel<HunterInfoViewModel>()
                val state by hunterInfoVm.state.collectAsStateWithLifecycle()
                HunterInfoScreen(
                    state = state,
                    actions = hunterInfoVm.actions,
                    navController = navController )
            }
        }
        with(OfficeHunterRoute.Offices){
            composable(route){
                val officesVm = koinViewModel<OfficesViewModel>()
                val state by officesVm.state.collectAsStateWithLifecycle()
                OfficesScreen(
                    state = state,
                    actions = officesVm.actions,
                )
            }
        }
        with(OfficeHunterRoute.Hunted){
            composable(route){
                val state by huntedVm.state.collectAsStateWithLifecycle()
                HuntedScreen(
                    actions = huntedVm.actions,
                    state = state,
                    filteredHuntedData = filteredHuntedData,
                )
            }
        }
        with(OfficeHunterRoute.Hunt){
            composable(route){
                val huntVm = koinViewModel<HuntViewModel>()
                val state by huntVm.state.collectAsStateWithLifecycle()
                HuntScreen(
                    state = state,
                    actions = huntVm.actions
                )
            }
        }
        with(OfficeHunterRoute.Stats){
            composable(route){
                val statsVm = koinViewModel<StatsViewModel>()
                val state by statsVm.state.collectAsStateWithLifecycle()
                StatsScreen(
                    state = state,
                    actions = statsVm.actions,
                    )
            }
        }
        with(OfficeHunterRoute.Profile){
            composable(route){
                val profileVm = koinViewModel<ProfileViewModel>()
                val state by profileVm.state.collectAsStateWithLifecycle()
                val usersData by profileVm.usersData.collectAsStateWithLifecycle()
                val achievement by profileVm.achievements.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    usersData = usersData,
                    achievements = achievement,
                    actions = profileVm.actions,
                    navController = navController)
            }
        }
    }
}
