package com.officehunter.ui.screens.hunted

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.composables.AdvanceStarRow
import com.officehunter.ui.composables.RarityBadge
import com.officehunter.ui.theme.SilverGradient
import com.officehunter.utils.Formatter
import com.officehunter.utils.getRarityBrush
import com.officehunter.utils.getRarityImage
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun HuntedDetailDialog(hunted: Hunted?, showDialog:Boolean, onClose: ()->Unit) {
    if (showDialog && hunted != null) {
        val isUndiscovered = hunted.rarity == Rarity.UNDISCOVERED
        Dialog(onDismissRequest = {onClose()}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            // Custom layout for the dialog
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val rarityBrush = getRarityBrush(hunted.rarity)
                        if (rarityBrush != null) {
                            drawRect(
                                brush = SilverGradient,
                            )
                        }
                    },
                shape = RoundedCornerShape(0.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val rarityImage = getRarityImage(hunted.rarity)
                    if (rarityImage != null) {
                        Image(
                            painter = painterResource(rarityImage),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Background Image",
                        )
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
                        .padding(16.dp)
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AdvanceStarRow(hunted.rarity)
                    Spacer(Modifier.size(8.dp))
                    val onCardBackgroundColor = if (isUndiscovered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    Image(
                        painter = painterResource(R.drawable.mockuserimag),
                        modifier = Modifier
                            .height(336.dp)
                            .width(336.dp)
                            .border(
                                BorderStroke(3.dp, onCardBackgroundColor),
                                RoundedCornerShape(200.dp),
                            ),
                        contentDescription = "Hunted image",
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.size(8.dp))
                    StyledShadowText(
                        "\"${hunted.variant}\"",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        isUndiscovered
                    )
                    StyledShadowText(
                        "${hunted.name} ${hunted.surname}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        isUndiscovered
                    )
                    Spacer(Modifier.size(8.dp))
                    RarityBadge(hunted.rarity)
                    Spacer(Modifier.size(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatContainer(
                            "spawn rate",
                            Formatter.double2percentage(hunted.spawnRate),
                            isUndiscovered
                        )
                        StatContainer(
                            "found rate",
                            Formatter.double2percentage(hunted.foundRate),
                            isUndiscovered
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    StatContainer(
                        "found date",
                        if(hunted.foundDate!=null) Formatter.date2String(hunted.foundDate!!) else "NOT FOUND",
                        isUndiscovered
                    )
                }
            }
        }
    }
}

@Composable
fun StyledShadowText(
    text:String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    isUndiscovered:Boolean
) {
    Text(
        text = text,
        color = if(isUndiscovered) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        style = TextStyle(
            shadow = if(isUndiscovered) null else Shadow(
                color = MaterialTheme.colorScheme.tertiary,
                offset = Offset(0f, 1f),
                blurRadius = 4f
            ),
            fontStyle = FontStyle.Normal,
            lineHeight = 30.sp,
            letterSpacing = (-0.724).sp
        ),
    )
}

@Composable
fun StatContainer(
    label:String,
    value:String,
    isUndiscovered:Boolean){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StyledShadowText(
            label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            isUndiscovered
        )
        StyledShadowText(
            value,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            isUndiscovered
        )
    }
}