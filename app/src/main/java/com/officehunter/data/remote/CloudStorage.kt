package com.officehunter.data.remote

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CloudStorage {
    private val storage =  Firebase.storage
    private var storageRef = storage.reference
    var huntedImagesRef: StorageReference? = storageRef.child(huntedImagePath)

    fun getImageRef(image:String):StorageReference?{
        return huntedImagesRef?.child(image)
    }

    companion object {
        val huntedImagePath = "hunted_images"
    }
}