package com.officehunter.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BarIconType{
    TOP,
    BOTTOM
}
@Composable
fun BarIcon (
    text:String,
    @DrawableRes drawable:Int,
    barIconStyle: BarIconType,
    isClickable: Boolean = false,
    onClick: (() -> Unit)? = null,
){
    Box(Modifier.clickable{
        if (onClick != null && isClickable) {
            onClick()
        }
    }){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val icon = painterResource(drawable)
            val fontWeight = if(barIconStyle==BarIconType.BOTTOM) FontWeight.Medium else FontWeight.Bold
            val fontSize = if(barIconStyle==BarIconType.BOTTOM) 12.sp else 16.sp
            val color = if(isClickable) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp),
                colorFilter = if(isClickable) ColorFilter.tint(MaterialTheme.colorScheme.tertiary) else null
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text,
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = color,
            )
        }
    }

}