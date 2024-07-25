package com.officehunter.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Sailing
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.data.repositories.UserRepository
import com.officehunter.ui.OfficeHunterRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: OfficeHunterRoute,
    userRepository: UserRepository,
) {
    println(currentRoute.route == OfficeHunterRoute.Login.route)
    when (currentRoute.route) {
        OfficeHunterRoute.Login.route -> {}
        OfficeHunterRoute.SignUp.route -> {}
        OfficeHunterRoute.Questions.route -> {}
        else -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
        ) {
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color =  MaterialTheme.colorScheme.tertiary,
            )
            //Spacer(modifier = Modifier.fillMaxWidth())
            val image = painterResource(R.drawable.logov2)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .width(28.dp)
            )
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .width(28.dp)

            )
        }
        /*
        else -> CenterAlignedTopAppBar(
            title = {
                Text(
                    currentRoute.title,
                    fontWeight = FontWeight.Medium,
                )
            },
            navigationIcon = {
                if (navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                }
            },
            actions = {
                if (currentRoute.route == OfficeHunterRoute.Home.route) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                }
                if (currentRoute.route != OfficeHunterRoute.Settings.route) {
                    IconButton(onClick = { navController.navigate(OfficeHunterRoute.Settings.route) }) {
                        Icon(Icons.Outlined.Settings, "Settings")
                    }
                }
                if(currentRoute.route != OfficeHunterRoute.Login.route){
                    IconButton(onClick = {navController.navigate(OfficeHunterRoute.Login.route)}) {
                        Icon(Icons.Outlined.Sailing,"office hunter")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        */
    }
}
