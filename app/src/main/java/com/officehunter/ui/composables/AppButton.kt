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

@Composable
fun AppButton (
    label: String,
    onClick: () -> Unit,
) {
   Button(
       onClick = onClick,
       colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.tertiary
       ),
       modifier = Modifier.fillMaxWidth().height(52.dp)
   ) {
        Text(label, fontSize = 16.sp)
   }
}