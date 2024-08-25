package com.officehunter.data.remote

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CloudStorage {
    private val storage =  Firebase.storage
    private var storageRef = storage.reference
    var huntedImagesRef: StorageReference? = storageRef.child(huntedImagePath)

    fun getImageDownloadUrl(huntedId:String,onResult: (Result<Uri>) -> Unit){
        huntedImagesRef?.child("${huntedId}.jpg")?.downloadUrl?.addOnSuccessListener {
            onResult(Result.success(it))
        }?.addOnFailureListener {
            onResult(Result.failure(it))
        }

        /* FILE
            islandRef = storageRef.child("images/island.jpg")
            val localFile = File.createTempFile("images", "jpg")
            islandRef.getFile(localFile).addOnSuccessListener {
                // Local temp file has been created
            }.addOnFailureListener {
                // Handle any errors
            }
         */
    }

    fun uploadHuntedImageToCloud(huntedId:String, imageUri: Uri, onResult: (Result<Unit>) -> Unit){
        val newImageRef = huntedImagesRef?.child("${huntedId}.jpg")
        if(newImageRef!=null){
            val uploadTask = newImageRef.putFile(imageUri)
            uploadTask.addOnFailureListener {
                onResult(Result.success(Unit))
            }.addOnSuccessListener { taskSnapshot ->
                onResult(Result.failure(taskSnapshot?.error ?: Exception("Unknown error")))
            }
        }
    }

    companion object {
        const val huntedImagePath = "hunted_images"
    }
}