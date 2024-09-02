package com.officehunter.ui.composables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.R
import com.officehunter.data.database.Achievement
import java.util.Locale

@Composable
fun AchievementDialog(achievement: Achievement?, getAchievementImageUri: suspend (imageName: String) -> Uri?, onClose: ()->Unit){
    if(achievement!=null){
        Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .width(300.dp)
                    .background(MaterialTheme.colorScheme.onBackground),
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ){
                        IconButton(onClick = onClose){
                            Icon(
                                imageVector  = Icons.Filled.Close,
                                "close filter dialog",
                                tint=MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(32.dp).padding(4.dp)
                            )
                        }
                    }
                    Text(
                        text = achievement.name.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier=Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = achievement.description,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier=Modifier.padding(bottom = 16.dp)
                    )
                    AchievementImage(
                        achievement,
                        getAchievementImageUri,
                        size = 200,
                        circularProgressColor = MaterialTheme.colorScheme.secondary
                    )
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 20.dp, bottom = 32.dp)
                    ){
                        Text(
                            text = "+${achievement.pointValue}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        val pointsIcon = painterResource(R.drawable.points)
                        Image(
                            painter = pointsIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(46.dp)
                                .padding(start = 4.dp),
                        )
                    }
                }
            }
        }
    }
}