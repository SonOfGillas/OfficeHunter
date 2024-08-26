package com.officehunter.data.repositories

import android.net.Uri
import android.util.Log
import com.officehunter.data.remote.CloudStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(
    val cloudStorage: CloudStorage
) {
    /* TODO check if this method should be optimized and give a different Uri for Image already Downloaded*/
    suspend fun getHuntedImage(huntedId:String): Uri? {
        val deferredResult = CompletableDeferred<Uri?>()
        this.cloudStorage.getImageDownloadUrl(huntedId)
        { result ->
            result.onSuccess {
                deferredResult.complete(it)
            }.onFailure {
                Log.d("HuntedViewModel",it.message?:"fail to fetch image Uri")
                deferredResult.complete(null)
            }
        }
        return withContext(Dispatchers.IO) {
            deferredResult.await()
        }

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