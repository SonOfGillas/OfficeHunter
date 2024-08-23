package com.officehunter.ui.screens.offices

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.R
import com.officehunter.data.database.Office
import com.officehunter.ui.composables.map.AppMap
import com.officehunter.ui.composables.map.MarkerInfo
import com.officehunter.ui.theme.SilverGradient
import com.officehunter.utils.getRarityBrush
import org.osmdroid.util.GeoPoint

@Composable
fun OfficeMapDialog(officeToShow: Office?, onClose:()->Unit){
    if (officeToShow!=null){
        val officeMarkerInfo = MarkerInfo(
            GeoPoint(officeToShow.latitude, officeToShow.longitude)
        )

        Dialog(onDismissRequest = {onClose()}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .height(74.dp)
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primary
                            )
                            .padding(top = 12.dp)
                    ) {
                        Box(Modifier.clickable{onClose()}){
                            Image(
                                painter = painterResource(R.drawable.back),
                                modifier = Modifier
                                    .height(42.dp)
                                    .width(42.dp)
                                    .padding(start = 12.dp, top = 12.dp),
                                contentDescription = "Back Arrow",
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                    AppMap(
                        listOf<MarkerInfo>(officeMarkerInfo),
                        startingPosition =officeMarkerInfo.geoPoint
                    )
                }
            }
        }
    }
}