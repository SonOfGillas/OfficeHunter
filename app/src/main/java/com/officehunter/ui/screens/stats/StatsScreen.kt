package com.officehunter.ui.screens.stats

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.R
import com.officehunter.data.database.Office
import com.officehunter.data.remote.firestore.entities.User
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.AppSeparator
import com.officehunter.ui.composables.ButtonSize
import com.officehunter.ui.screens.offices.OfficeCard
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun StatsScreen(
    state: StatsState,
    actions: StatsActions
) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Hunted Founded",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
            Row (
                modifier = Modifier.padding(bottom = 20.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                PieChart(
                    modifier = Modifier.size(160.dp),
                    data = listOf(
                        Pie(label = "Not Founded",data = state.huntedNotFounded.toDouble(), color = MaterialTheme.colorScheme.onSurface),
                        Pie(label = "Founded",data = state.huntedFounded.toDouble(), color = MaterialTheme.colorScheme.primary),
                    ),
                    selectedScale = 1.2f,
                    scaleAnimEnterSpec = spring<Float>(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    style = Pie.Style.Stroke(width = 40.dp)
                )
                Spacer(modifier = Modifier.size(14.dp))
                Column {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Box (modifier= Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.primary)){}
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Founded",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.size(14.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Box (modifier= Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.onSurface)){}
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Not Founded",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Hunter Ranking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start
                )
            }
            if (state.currentUser != null){
                Box(modifier = Modifier.padding(horizontal = 8.dp)){
                    UserCard(user = state.currentUser, index = state.usersRank.indexOf(state.currentUser))
                }
            }
            AppSeparator()
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                itemsIndexed(state.usersRank) { index,user ->
                    UserCard(user, index = index )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    index: Int
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(24.dp),
        //modifier = Modifier.height(128.dp),
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)){
            Column {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ){
                        Text(
                            text = "${index+1}°",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "${user.name} ${user.surname}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            text = "${user.points}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        val pointIcon = painterResource(R.drawable.points)
                        Image(
                            painter = pointIcon,
                            contentDescription = null,
                            modifier = Modifier.width(30.dp),
                        )
                    }
                }
            }
        }
    }
}