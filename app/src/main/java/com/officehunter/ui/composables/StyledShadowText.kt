package com.officehunter.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

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