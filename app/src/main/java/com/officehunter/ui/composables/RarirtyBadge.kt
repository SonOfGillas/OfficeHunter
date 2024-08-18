package com.officehunter.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.theme.DarkPurple
import com.officehunter.ui.theme.commonBackground
import com.officehunter.ui.theme.commonMain
import com.officehunter.ui.theme.epicBackground
import com.officehunter.ui.theme.epicMain
import com.officehunter.ui.theme.legendaryBackground
import com.officehunter.ui.theme.legendaryMain
import com.officehunter.ui.theme.rareBackground
import com.officehunter.ui.theme.rareMain
import com.officehunter.ui.theme.ultraRareBackground
import com.officehunter.ui.theme.ultraRareMain
import com.officehunter.ui.theme.uncommonBackground
import com.officehunter.ui.theme.uncommonMain
import com.officehunter.ui.theme.undiscoveredBackground
import com.officehunter.ui.theme.veryRareBackground
import com.officehunter.ui.theme.veryRareMain

@Composable
fun RarityBadge(rarity: Rarity){
    val badgeColors = getBadgeColors(rarity)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = badgeColors.background
        ),
        shape = RoundedCornerShape(30.dp),
        border =  BorderStroke(3.dp, badgeColors.main),
    ){
        Text(
            text = rarity.formattedName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = badgeColors.main,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
        )
    }
}

class BadgeColors (
    val main : Color,
    val background: Color
)

fun getBadgeColors(rarity: Rarity): BadgeColors {
    return when(rarity){
        Rarity.COMMON -> BadgeColors(commonMain, commonBackground)
        Rarity.UNCOMMON -> BadgeColors(uncommonMain, uncommonBackground)
        Rarity.RARE -> BadgeColors(rareMain, rareBackground)
        Rarity.VERY_RARE -> BadgeColors(veryRareMain, veryRareBackground)
        Rarity.ULTRA_RARE -> BadgeColors(ultraRareMain, ultraRareBackground)
        Rarity.EPIC -> BadgeColors(epicMain, epicBackground)
        Rarity.LEGENDARY -> BadgeColors(legendaryMain, legendaryBackground)
        Rarity.UNDISCOVERED -> BadgeColors(DarkPurple, undiscoveredBackground)
    }
}