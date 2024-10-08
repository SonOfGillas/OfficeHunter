package com.officehunter.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.officehunter.R

@Composable
fun AppSeparator(){
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ){
        Spacer(modifier = Modifier
            .width(136.dp)
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onBackground))
        Spacer(modifier = Modifier.size(16.dp))
        val appLogo = painterResource(R.drawable.logov2)
        Image(
            painter = appLogo,
            contentDescription = null,
            modifier = Modifier.width(32.dp),
        )
        Spacer(modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier
            .width(124.dp)
            .height(4.dp)
            .background(MaterialTheme.colorScheme.onBackground))
    }
}