package com.officehunter.ui.composables

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.officehunter.R
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity

@Composable
fun HuntedImage(
    hunted: Hunted,
    getHuntedImageUri: suspend (hunted:Hunted)-> Uri?,
    size: Int = 100
){
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(hunted) {
        isLoading = true
        imageUri = getHuntedImageUri(hunted)
        isLoading = false
    }

    val boarderRadius = (size/2).toInt().dp
    Box(modifier = Modifier
        .width(size.dp)
        .height(size.dp)
        .border(BorderStroke(3.dp,
            if (hunted.rarity == Rarity.UNDISCOVERED) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
            ), RoundedCornerShape(boarderRadius),)
        .clip(RoundedCornerShape(boarderRadius))
    ){
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            imageUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Image(
                    painter = painterResource(R.drawable.no_image_available),
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}