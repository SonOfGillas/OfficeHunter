package com.officehunter.ui.screens.hunted

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.ui.composables.SimpleStarRow
import com.officehunter.ui.theme.GoldGradient
import com.officehunter.ui.theme.SilverGradient
import com.officehunter.utils.getRarityBrush
import com.officehunter.utils.getRarityImage

@Composable
fun HuntedScreen(
    actions: HuntedActions,
    huntedData: HuntedRepositoryData
) {
    Scaffold { contentPadding ->
        /*
        Column(
            modifier = Modifier.padding(contentPadding)
            .width(100.dp).height(100.dp)
                .drawBehind {
                    drawRoundRect(
                        brush = SilverGradient,
                        cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                    )
                },
        ) {}
        */
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
            .size(240.dp)
            .fillMaxWidth()
            .drawBehind {
                val rarityBrush = getRarityBrush(item.rarity)
                if (rarityBrush != null) {
                    drawRoundRect(
                        brush = SilverGradient,
                        cornerRadius = CornerRadius(30.dp.toPx(), 30.dp.toPx())
                    )
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.onBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            val rarityImage = getRarityImage(item.rarity)
            if(rarityImage != null){
                Image(
                    painter = painterResource(rarityImage),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleStarRow(item.rarity)
                Spacer(Modifier.size(8.dp))
                val onCardBackgroundColor = if (item.rarity == Rarity.UNDISCOVERED) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
                Box(modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .border(BorderStroke(3.dp,onCardBackgroundColor),RoundedCornerShape(50.dp),)
                ){
                    Image(
                        painter = painterResource(R.drawable.good_morning),
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.size(8.dp))
                Text(
                    "\"${item.variant}\"",
                    color = onCardBackgroundColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "${item.name} ${item.surname}",
                    color = onCardBackgroundColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

            }
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