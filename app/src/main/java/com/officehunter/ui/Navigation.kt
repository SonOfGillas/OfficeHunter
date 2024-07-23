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
import com.officehunter.ui.screens.profile.ProfileScreen
import com.officehunter.ui.screens.profile.ProfileViewModel
import com.officehunter.ui.screens.questions.QuestionsScreen
import com.officehunter.ui.screens.questions.QuestionsViewModel
import com.officehunter.ui.screens.signUp.SignUpScreen
import com.officehunter.ui.screens.signUp.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

sealed class OfficeHunterRoute(
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
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
    data object Questions : OfficeHunterRoute("questions", "Questions")
    data object Profile : OfficeHunterRoute("profile", "Profile")

    companion object {
        val routes = setOf(Home, TravelDetails, AddTravel, Settings,Login,SignUp, Questions, Profile)
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
        startDestination = OfficeHunterRoute.Login.route,
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
        with(OfficeHunterRoute.Questions){
            composable(route){
                val questionsVm = koinViewModel<QuestionsViewModel>()
                val state by questionsVm.state.collectAsStateWithLifecycle()
                QuestionsScreen(
                    state = state,
                    actions = questionsVm.actions,
                    navController = navController )
            }
        }
        with(OfficeHunterRoute.Profile){
            composable(route){
                val profileVm = koinViewModel<ProfileViewModel>()
                val state by profileVm.state.collectAsStateWithLifecycle()
                val userList by profileVm.userList.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    userList = userList,
                    actions = profileVm.actions,
                    navController = navController)
            }
        }
    }
}
