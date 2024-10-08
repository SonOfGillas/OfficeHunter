package com.officehunter.ui.composables.map


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.officehunter.R
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint
import java.io.FileNotFoundException
import java.io.InputStream


data class MarkerInfo(
    val geoPoint: GeoPoint,
    //val iconUri: Uri? = null,
    @DrawableRes val icon: Int? = null,
    val onClick: (()->Unit)? = null
)

val DefaultMapStartingPosition = GeoPoint(44.148357, 12.235488)

@Composable
fun AppMap(
    markersInfo: List<MarkerInfo> = emptyList<MarkerInfo>(),
    startingPosition: GeoPoint = DefaultMapStartingPosition,
    startingZoom: Double = 15.0
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val cameraState = rememberCameraState {
            geoPoint = startingPosition
            zoom = startingZoom
        }

        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState
        ){
            for (makerInfo in markersInfo){
                AppMarker(
                    MarkerInfo(
                        makerInfo.geoPoint, icon = makerInfo.icon, onClick = makerInfo.onClick
                    )
                )
            }
        }
    }
}


@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun AppMarker(markerInfo: MarkerInfo){
    val context = LocalContext.current

    val appMarkerState = rememberMarkerState(
        geoPoint =  markerInfo.geoPoint,
        rotation = 90f
    )

    val appMarkerIcon: Drawable? by remember {
        mutableStateOf(
            markerInfo.icon?.let {
                val originalDrawable = context.getDrawable(it)
                resizeDrawable(context, originalDrawable, 100, 100) // Set your desired width and height
            }
        )
    }


    Marker(
        state = appMarkerState,
        //icon = if(markerInfo.iconUri != null) getDrawableFromUri(context,markerInfo.iconUri) else null,
        icon = appMarkerIcon,
        onClick = {
            markerInfo.onClick?.let { it() }
            false
        }
    )
}

fun resizeDrawable(context: Context, drawable: Drawable?, width: Int, height: Int): Drawable? {
    if (drawable == null) return null
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDrawable(context.resources, bitmap)
}

fun getDrawableFromUri(context: Context, uri: Uri): Drawable? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        BitmapDrawable(context.resources, bitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}