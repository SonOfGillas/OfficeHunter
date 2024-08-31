package com.officehunter.ui.screens.hunt

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.composables.AdvanceStarRow
import com.officehunter.ui.composables.AchievementImage
import com.officehunter.ui.composables.HuntedImage
import com.officehunter.ui.composables.RarityBadge
import com.officehunter.ui.composables.StyledShadowText
import com.officehunter.ui.theme.SilverGradient
import com.officehunter.utils.getRarityBrush
import com.officehunter.utils.getRarityImage

@Composable
fun HuntDialog(state: HuntState, actions: HuntActions) {
    val hunted = state.selectedHunted
    val question = state.question
    if (hunted != null) {
        val isUndiscovered = hunted.rarity == Rarity.UNDISCOVERED
        Dialog(onDismissRequest = actions::closeHunt, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(bottom = 4.dp),
                ) {
                    val rarityImage = getRarityImage(hunted.rarity)
                    if (rarityImage != null) {
                        Image(
                            painter = painterResource(rarityImage),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentDescription = "Background Image",
                        )
                    }
                    val rarityBrush = getRarityBrush(hunted.rarity)
                    if(rarityBrush != null){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                                .drawBehind {
                                    val rarityBrush = getRarityBrush(hunted.rarity)
                                    drawRect(
                                        brush = SilverGradient,
                                    )
                                },
                        ) {}
                    }
                }

                Box(Modifier.clickable{actions.closeHunt()}){
                    Image(
                        painter = painterResource(R.drawable.back),
                        modifier = Modifier
                            .height(42.dp)
                            .width(42.dp)
                            .padding(start = 12.dp, top = 12.dp),
                        contentDescription = "Back Arrow",
                        colorFilter = ColorFilter.tint(
                            if (isUndiscovered)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(top = 12.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AdvanceStarRow(hunted.rarity)
                    Spacer(Modifier.size(8.dp))
                    val onCardBackgroundColor = if (isUndiscovered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    HuntedImage(
                        hunted = hunted,
                        getHuntedImageUri = actions::getHuntedImage,
                        size = 200
                    )
                    Spacer(Modifier.size(8.dp))
                    StyledShadowText(
                        "\"${hunted.variant}\"",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        isUndiscovered
                    )
                    Spacer(Modifier.size(12.dp))
                    RarityBadge(hunted.rarity)
                    Spacer(Modifier.size(12.dp))
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onSurface)
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (question != null){
                                Text(
                                    text = "${question.questionType.questionString}:",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 36.dp, top = 24.dp).fillMaxWidth()
                                )
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(1),
                                    verticalArrangement = Arrangement.spacedBy(24.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
                                ) {
                                    items(question.answers) { answer ->
                                        Column (
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ){
                                            if(question.answers.first() == answer){
                                                Divider(
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(bottom = 24.dp)
                                                )
                                            }
                                            Text(
                                                text = answer.description,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Divider(
                                                color = MaterialTheme.colorScheme.secondary,
                                                thickness = 1.dp,
                                                modifier = Modifier.padding(top = 24.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

        }
    }
}