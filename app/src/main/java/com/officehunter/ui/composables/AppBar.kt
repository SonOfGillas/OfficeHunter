package com.officehunter.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
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

    /*common style settings*/
    val backgroundColor = MaterialTheme.colorScheme.primary

    when (currentRoute.route) {
        OfficeHunterRoute.Splash.route -> {}
        OfficeHunterRoute.Login.route -> {}
        OfficeHunterRoute.SignUp.route -> {}
        OfficeHunterRoute.HunterInfo.route -> {}
        OfficeHunterRoute.Hunted.route -> {}
        else -> Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .height(74.dp)
                .fillMaxWidth()
                .background(
                    color = backgroundColor
                )
                .padding(top = 12.dp)
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
                //BarIcon(coffee, R.drawable.coffee, barIconStyle = BarIconType.TOP)
                Spacer(modifier = Modifier.size(18.dp))
                BarIcon(points, R.drawable.points, barIconStyle = BarIconType.TOP)
            }
        }
    }
}
