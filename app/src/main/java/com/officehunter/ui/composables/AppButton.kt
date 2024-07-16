package com.officehunter.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    fillMaxWidth: Boolean = true
) {
    val modifier = Modifier.height(buttonSize.size.dp)

   Button(
       onClick = onClick,
       colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.tertiary
       ),
       modifier = if(fillMaxWidth){ modifier.fillMaxWidth() } else { modifier }
   ) {
        Text(label, fontSize = 16.sp)
   }
}