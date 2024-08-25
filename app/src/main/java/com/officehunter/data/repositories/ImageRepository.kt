package com.officehunter.data.repositories

import android.net.Uri
import com.officehunter.data.remote.CloudStorage

class ImageRepository(
    val cloudStorage: CloudStorage
) {
    /* TODO check if this method should be optimized and give a different Uri for Image already Downloaded*/
    fun getHuntedImage(huntedId:String, onResult: (Result<Uri>)->Unit){
        this.cloudStorage.getImageDownloadUrl(huntedId,onResult)
    }

    /* TODO check if image must be save in the storage or camera temp file can be used for all the images*/
    fun addHuntedImage(huntedId:String, imageUri: Uri?, onResult: (Result<Unit>)->Unit){
        if (imageUri!=null){
            cloudStorage.getImageDownloadUrl(huntedId){
                result ->
                    result.onSuccess {
                        /*Image already uploaded*/
                        onResult(Result.success(Unit))
                    }.onFailure {
                        cloudStorage.uploadHuntedImageToCloud(huntedId,imageUri,onResult)
                    }
            }
        }
    }
}