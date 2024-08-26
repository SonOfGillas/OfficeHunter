package com.officehunter.ui.screens.hunt

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.composables.AdvanceStarRow
import com.officehunter.ui.composables.HuntedImage
import com.officehunter.ui.composables.RarityBadge
import com.officehunter.ui.composables.StatContainer
import com.officehunter.ui.composables.StyledShadowText
import com.officehunter.ui.theme.SilverGradient
import com.officehunter.utils.Formatter
import com.officehunter.utils.getRarityBrush
import com.officehunter.utils.getRarityImage

@Composable
fun HuntDialog(hunted: Hunted?, getHuntedImageUri: suspend (hunted: Hunted)-> Uri?, onClose: ()->Unit) {
    if (hunted != null) {
        val isUndiscovered = hunted.rarity == Rarity.UNDISCOVERED
        Dialog(onDismissRequest = {onClose()}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
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
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(),
                            contentDescription = "Background Image",
                        )
                    }
                    val rarityBrush = getRarityBrush(hunted.rarity)
                    if(rarityBrush != null){
                        Box(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f).drawBehind {
                                val rarityBrush = getRarityBrush(hunted.rarity)
                                drawRect(
                                    brush = SilverGradient,
                                )
                            },
                        ) {}
                    }
                }

                Box(Modifier.clickable{onClose()}){
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
                        getHuntedImageUri = getHuntedImageUri,
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
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurface)
                    ){

                    }
                }

        }
    }
}