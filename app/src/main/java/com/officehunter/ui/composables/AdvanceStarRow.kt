package com.officehunter.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Rarity

@Composable
fun AdvanceStarRow(rarity: Rarity){
    val numberOfStars = rarity.ordinal+1
    Row (
        verticalAlignment = Alignment.Bottom
    ){
        repeat(numberOfStars){
            Image(
                painter = painterResource(
                    if(rarity == Rarity.UNDISCOVERED)
                        R.drawable.gold_star_light_boarder
                    else
                        R.drawable.gold_star
                ),
                contentDescription = null,
                modifier = Modifier
                    .width(getStarSize(rarity,it))
                    .height(getStarSize(rarity,it))
            )
        }
    }
}

fun getStarSize(rarity: Rarity,index:Int): Dp {
    return when(rarity){
        Rarity.COMMON -> 36.dp
        Rarity.UNCOMMON -> 36.dp
        Rarity.RARE -> when(index){
            1-> 46.dp
            else-> 36.dp
        }
        Rarity.VERY_RARE -> when(index){
            1-> 46.dp
            2-> 46.dp
            else-> 36.dp
        }
        Rarity.ULTRA_RARE -> when(index){
            1-> 46.dp
            2 -> 56.dp
            3-> 46.dp
            else-> 36.dp
        }
        Rarity.EPIC -> when(index){
            1-> 46.dp
            2 -> 56.dp
            3 -> 56.dp
            4-> 46.dp
            else-> 36.dp
        }
        Rarity.LEGENDARY -> when(index){
            1-> 46.dp
            2 -> 56.dp
            3 -> 64.dp
            4 -> 56.dp
            5-> 46.dp
            else-> 36.dp
        }
        Rarity.UNDISCOVERED -> when(index){
            1-> 36.dp
            2 -> 46.dp
            3 -> 56.dp
            4 -> 56.dp
            5 -> 46.dp
            6 -> 36.dp
            else-> 24.dp
        }
    }

}