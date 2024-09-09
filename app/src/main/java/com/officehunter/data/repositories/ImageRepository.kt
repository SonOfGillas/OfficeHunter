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
        this.cloudStorage.getHuntedImageDownloadUrl(huntedId)
        { result ->
            result.onSuccess {
                deferredResult.complete(it)
            }.onFailure {
                deferredResult.complete(null)
            }
        }
        return withContext(Dispatchers.IO) {
            deferredResult.await()
        }
    }

    suspend fun getAchievementImage(imageName:String): Uri? {
        val deferredResult = CompletableDeferred<Uri?>()
        this.cloudStorage.getAchievementImageDownloadUrl(imageName)
        { result ->
            result.onSuccess {
                deferredResult.complete(it)
            }.onFailure {
                deferredResult.complete(null)
            }
        }
        return withContext(Dispatchers.IO) {
            deferredResult.await()
        }
    }

    fun addHuntedImage(huntedId:String, imageUri: Uri?, onResult: (Result<Unit>)->Unit){
        if (imageUri!=null){
            cloudStorage.getHuntedImageDownloadUrl(huntedId){
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