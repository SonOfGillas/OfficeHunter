package com.officehunter.ui.screens.offices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.R
import com.officehunter.data.database.Office
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.ButtonSize
import com.officehunter.ui.screens.hunted.HuntedCard


@Composable
fun OfficesScreen(
    state: OfficesState,
    actions: OfficesActions,
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
                "Favorite Office",
                fontSize = 20.sp,
                color =  MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "you’ll receive 2x Points when hunting there",
                fontSize = 16.sp,
                color =  MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(16.dp))
            state.favoriteOffice?.let {
                Box (Modifier.padding(8.dp)){
                    OfficeCard(office = it, isFavorite = true, onOpenInMap = actions::showOfficePosition)
                }
            }
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ){
                Spacer(modifier = Modifier
                    .width(136.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.onBackground))
                Spacer(modifier = Modifier.size(16.dp))
                val appLogo = painterResource(R.drawable.logov2)
                Image(
                    painter = appLogo,
                    contentDescription = null,
                    modifier = Modifier.width(32.dp),
                )
                Spacer(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier
                    .width(124.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.onBackground))
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                modifier = Modifier.padding(contentPadding)
            ) {
                items(state.otherOffices) { office ->
                    OfficeCard(office = office, onOpenInMap = actions::showOfficePosition){
                        actions.setFavoriteOffice(office)
                    }
                }
            }
        }
        OfficeMapDialog(officeToShow = state.officeToShow, onClose = actions::closePositionDialog )
    }
}

@Composable
fun OfficeCard(
    office: Office,
    isFavorite: Boolean = false,
    onOpenInMap: (office:Office)->Unit,
    onSelectAsFavorite: ((office:Office)->Unit)? = null,
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
                    modifier = Modifier.fillMaxWidth()
                ){
                    val officeIcon = painterResource(R.drawable.offices)
                    Image(
                        painter = officeIcon,
                        contentDescription = null,
                        modifier = Modifier.width(24.dp),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Row (modifier = Modifier.weight(1f)){
                        Column (
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                        ){
                            Text(
                                text = office.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = office.street,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                    Box(modifier = Modifier.clickable {
                        if (!isFavorite && onSelectAsFavorite != null){
                            onSelectAsFavorite.invoke(office)
                        }
                    }){
                        Icon(
                            imageVector = if(isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite office",
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Row {
                        Text(
                            text = if (isFavorite) "2x" else "1x",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(end = 2.dp),
                            textAlign = TextAlign.Start
                        )
                        val pointsIcon = painterResource(R.drawable.points)
                        Image(
                            painter = pointsIcon,
                            contentDescription = null,
                            modifier = Modifier.width(32.dp),
                        )
                    }
                    AppButton(
                        "Open in map",
                        onClick = {onOpenInMap(office)},
                        buttonSize = ButtonSize.MEDIUM,
                        fillMaxWidth = false,
                        icon = Icons.Outlined.Map
                    )
                }
            }
        }
    }
}