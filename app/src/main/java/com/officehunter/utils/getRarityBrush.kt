package com.officehunter.utils

import androidx.compose.ui.graphics.Brush
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.theme.GoldGradient
import com.officehunter.ui.theme.SilverGradient

fun getRarityBrush(rarity: Rarity): Brush?{
    return when(rarity){
        Rarity.RARE -> SilverGradient
        Rarity.VERY_RARE -> GoldGradient
        else -> null
    }
}