package com.officehunter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.officehunter.data.repositories.SettingsRepository
import com.officehunter.data.repositories.UserRepository
import com.officehunter.ui.composables.AppBar
import com.officehunter.utils.LocationService
import com.officehunter.ui.OfficeHunterNavGraph
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.ui.composables.BottomBar
import com.officehunter.ui.screens.hunted.HuntedViewModel
import com.officehunter.ui.theme.OfficeHunterTheme
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    private lateinit var locationService: LocationService
    private lateinit var userRepository: UserRepository
    private lateinit var settingRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = get<LocationService>()
        userRepository = get<UserRepository>()
        settingRepository = get<SettingsRepository>()
        val usersData = userRepository.userRepositoryData.asStateFlow()
        val settings = settingRepository.settings.asStateFlow()

        setContent {
            OfficeHunterTheme(
                darkTheme = settings.collectAsStateWithLifecycle().value.isDarkTheme,
                dynamicColor = false) {
                Surface(
                    color = Color(0xFF261132),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            OfficeHunterRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: OfficeHunterRoute.Splash
                        }
                    }
                    
                    Scaffold(
                        topBar = {AppBar(
                            navController,
                            currentRoute,
                            usersData.collectAsStateWithLifecycle(),
                        )},
                        bottomBar = { BottomBar(navController, currentRoute)}
                    ) {
                        contentPadding ->
                        OfficeHunterNavGraph(
                            navController,
                            modifier =  Modifier.padding(contentPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationService.pauseLocationRequest()
    }

    override fun onResume() {
        super.onResume()
        locationService.resumeLocationRequest()
    }
}
