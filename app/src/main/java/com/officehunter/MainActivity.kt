package com.officehunter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.traveldiary.ui.composables.AppBar
import com.example.traveldiary.utils.LocationService
import com.officehunter.ui.OfficeHunterNavGraph
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.ui.theme.OfficeHunterTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    private lateinit var locationService: LocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = get<LocationService>()

        setContent {
            OfficeHunterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            OfficeHunterRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: OfficeHunterRoute.Home
                        }
                    }

                    Scaffold(
                        topBar = { AppBar(navController, currentRoute) }
                    ) { contentPadding ->
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
