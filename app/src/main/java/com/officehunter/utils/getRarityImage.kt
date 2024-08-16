package com.officehunter.utils

import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Rarity


fun getRarityImage(rarity: Rarity): Int? {
    return when(rarity){
        Rarity.COMMON -> R.drawable.wood
        Rarity.UNCOMMON -> R.drawable.copper
        Rarity.ULTRA_RARE -> R.drawable.ruby
        Rarity.EPIC -> R.drawable.emerald
        Rarity.LEGENDARY -> R.drawable.diamond
        Rarity.UNDISCOVERED -> R.drawable.space
        else -> null
    }
}
