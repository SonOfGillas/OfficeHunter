package com.officehunter.ui.screens.hunted

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.data.database.Place
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.ui.composables.ImageWithPlaceholder
import com.officehunter.ui.composables.Size
import com.officehunter.ui.screens.home.NoItemsPlaceholder
import com.officehunter.ui.screens.home.TravelItem
import com.officehunter.ui.screens.hunted.HuntedActions
import com.officehunter.ui.theme.commonBackground

@Composable
fun HuntedScreen(
    actions: HuntedActions,
    huntedData: HuntedRepositoryData
) {
    Scaffold { contentPadding ->
        if (huntedData.huntedList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(huntedData.huntedList) { hunted ->
                    HuntedCard(hunted){ println("click") }
                }
            }
        } else {
            NoItemsPlaceholder(Modifier.padding(contentPadding))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuntedCard(item: Hunted, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(180.dp)
            //.border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(30.dp))
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = commonBackground
        ),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //val imageUri = Uri.parse(item.imageUri)
            //ImageWithPlaceholder(imageUri, Size.Sm)
            /*if (imageUri.path?.isNotEmpty() == true) {
                AsyncImage(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    "Travel picture",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    Icons.Outlined.Image,
                    "Travel picture",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(20.dp)
                )
            }*/
            Spacer(Modifier.size(8.dp))
            Text(
                item.name,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoItemsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "No Hunted Founded",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
