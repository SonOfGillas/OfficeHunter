package com.officehunter.ui.screens.splash

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.ui.OfficeHunterRoute

@Composable
fun SplashScreen(
    state: SplashState,
    navController: NavHostController
){
    if (state == SplashState.Ready){
        navController.navigate(OfficeHunterRoute.Profile.route)
    }


    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
    ) { contentPadding ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(contentPadding).fillMaxSize()
        ){
            val appLogo = painterResource(R.drawable.logov2)
            Image(
                painter = appLogo,
                contentDescription = null,
            )
        }
    }
}