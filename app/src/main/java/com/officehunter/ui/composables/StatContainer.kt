package com.officehunter.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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