package com.officehunter.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonSize(val size: Int) {
    MEDIUM(38),
    BIG(52)
}

@Composable
fun AppButton (
    label: String,
    onClick: () -> Unit,
    buttonSize: ButtonSize = ButtonSize.BIG,
    fillMaxWidth: Boolean = true,
    icon: ImageVector? = null,
    customColors: ButtonColors? = null
) {
    val modifier = Modifier.height(buttonSize.size.dp)
    val iconSize = when (buttonSize){
        ButtonSize.MEDIUM -> 24.dp
        ButtonSize.BIG -> 36.dp
    }

   Button(
       onClick = onClick,
       colors = customColors ?: ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
       ),
       modifier = if(fillMaxWidth){ modifier.fillMaxWidth() } else { modifier }
   ) {
       Text(label, fontSize = 16.sp)
       icon?.let {
           Icon(
               imageVector = icon,
               contentDescription = "",
               modifier = Modifier
                   .padding(start = 4.dp)
                   .width(iconSize)
                   .height(iconSize),
               tint = MaterialTheme.colorScheme.background
           )
       }
   }
}