package com.officehunter.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.data.repositories.UserRepositoryData
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.utils.Formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    currentRoute: OfficeHunterRoute,
    usersData: State<UserRepositoryData>,
    ) {

    val currentUser = usersData.value.currentUser
    val coffee = Formatter.int2string(currentUser?.coffee?.toInt() ?: 0)
    val points = Formatter.int2string(currentUser?.points?.toInt() ?: 0)

    val height = 74.dp
    val bagroundColor = MaterialTheme.colorScheme.primary
    val paddingTop = 12.dp

    when (currentRoute.route) {
        OfficeHunterRoute.Login.route -> {}
        OfficeHunterRoute.SignUp.route -> {}
        OfficeHunterRoute.Questions.route -> {}
        OfficeHunterRoute.Hunted.route ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(86.dp)
                    .fillMaxWidth()
                    .background(
                        color = bagroundColor
                    )
                    .padding(horizontal = 12.dp),
            ) {
                Box(modifier = Modifier.weight(0.7f)){
                    AppTextField(
                        value = "",
                        onValueChange = {
                                it -> println(it)
                        },
                        label = "Search",
                        preset = AppTextFieldPreset.SEARCH
                    )
                }
                Box(Modifier.clickable{
                    println("FilterClick")
                }){
                    Image(
                        painter = painterResource(R.drawable.filter),
                        modifier = Modifier
                            .height(42.dp)
                            .width(42.dp),
                        contentDescription = "FilterButton",
                    )
                }
            }
        else -> Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(
                    color = bagroundColor
                )
                .padding(top = paddingTop)
        ) {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color =  MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 24.dp)
            )
            Row(
                modifier = Modifier.padding(end=24.dp)
            ) {
                BarIcon(coffee, R.drawable.coffee, barIconStyle = BarIconType.TOP)
                Spacer(modifier = Modifier.size(18.dp))
                BarIcon(points, R.drawable.points, barIconStyle = BarIconType.TOP)
            }
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
