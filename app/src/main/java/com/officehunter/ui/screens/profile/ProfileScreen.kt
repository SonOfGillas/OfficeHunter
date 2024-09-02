package com.officehunter.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.data.database.Achievement
import com.officehunter.data.repositories.UserRepositoryData
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.ButtonSize
import com.officehunter.ui.composables.AchievementImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    usersData: UserRepositoryData,
    achievements: List<Achievement>,
    actions: ProfileActions,
    navController: NavHostController
){
    if (state.profilePhase == ProfilePhase.USER_NOT_LOGGED){
        actions.setToIdle()
        navController.navigate(OfficeHunterRoute.Login.route)
    }

    val username = if (usersData.currentUser != null) "${usersData.currentUser.name} ${usersData
        .currentUser.surname}" else ""

    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ){
            Row(
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                val image = painterResource(R.drawable.logov2)
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.size(24.dp))

                Column {
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        username,
                        fontSize = 20.sp,
                        color =  MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    AppButton(
                        label = "logout",
                        onClick = {actions.logout()},
                        buttonSize = ButtonSize.MEDIUM,
                        fillMaxWidth = false
                    )
                }
            }
            Text(
                "Achievements",
                fontSize = 24.sp,
                color =  MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (achievements.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(4.dp, 4.dp, 4.dp, 80.dp),
                    modifier = Modifier.padding(contentPadding)
                ) {
                    items(achievements) { achievement ->
                        AchievementItem(achievement,actions::getAchievementsIcon)
                    }
                }
            }
        }
    }

}

@Composable
fun AchievementItem(achievement:Achievement, getAchievementImageUri: suspend (imageName: String) -> Uri?){
    val achievementUnlocked = achievement.numberOfTimesAchieved>0
    Row {
        AchievementImage(
            achievement,
            getAchievementImageUri,
            achievementUnlocked
        )
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = achievement.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(
                    lineHeight = 1.0.em
                )
            )
            Text(
                text = achievement.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(
                    lineHeight = 1.0.em
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "+${achievement.pointValue}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val pointsIcon = painterResource(R.drawable.points_reversed_color)
                Image(
                    painter = pointsIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 2.dp, end = 1.dp),
                )
                if(achievementUnlocked){
                    Text(
                        text = "x${achievement.numberOfTimesAchieved}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}