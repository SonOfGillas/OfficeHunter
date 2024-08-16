package com.officehunter.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Rarity

@Composable
fun SimpleStarRow(rarity: Rarity){
    val numberOfStars = rarity.ordinal+1
    Row {
        repeat(numberOfStars){
            Image(
                painter = painterResource(R.drawable.star),
                contentDescription = null,
                modifier = Modifier
                    .width(14.dp)
                    .height(14.dp),
                colorFilter = ColorFilter.tint(
                    if (rarity==Rarity.UNDISCOVERED)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}