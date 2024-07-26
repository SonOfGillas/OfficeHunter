package com.officehunter.ui.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.ui.OfficeHunterRoute

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: OfficeHunterRoute,
) {

    println(currentRoute.route == OfficeHunterRoute.Login.route)


    when (currentRoute.route) {
        OfficeHunterRoute.Login.route -> {}
        OfficeHunterRoute.SignUp.route -> {}
        OfficeHunterRoute.Questions.route -> {}
        else ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    )
                    .padding(top = 12.dp)
            ) {
                BarIcon(
                    "Offices",
                    R.drawable.offices,
                    BarIconType.BOTTOM,
                    isClickable = currentRoute.route != OfficeHunterRoute.Login.route
                    )
                BarIcon(
                    "Hunted",
                    R.drawable.necktie,
                    BarIconType.BOTTOM,
                    isClickable = currentRoute.route != OfficeHunterRoute.Login.route
                    )
                BarIcon(
                    "Hunt",
                    R.drawable.hunt,
                    BarIconType.BOTTOM,
                    isClickable = currentRoute.route != OfficeHunterRoute.Login.route
                    )
                BarIcon(
                    "Stats",
                    R.drawable.stats,
                    BarIconType.BOTTOM,
                    isClickable = currentRoute.route != OfficeHunterRoute.Login.route
                )
                BarIcon(
                    "Profile",
                    R.drawable.profile,
                    BarIconType.BOTTOM,
                    isClickable = currentRoute.route != OfficeHunterRoute.Profile.route
                )
        }
    }
}