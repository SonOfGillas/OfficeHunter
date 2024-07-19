package com.officehunter.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Firestore {
    private var db = Firebase.firestore

    fun insert(collectionName:String, document: HashMap<String,Any>, onResult: (Result<Unit>) -> Unit){
        db.collection(collectionName).add(document)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { e -> onResult(Result.failure(e))}
    }

    fun read(collectionName: String, onResult: (Result<QuerySnapshot>) -> Unit){
        db.collection(collectionName).get()
            .addOnSuccessListener { onResult(Result.success(it)) }
            .addOnFailureListener { e -> onResult(Result.failure(e))}
    }
}