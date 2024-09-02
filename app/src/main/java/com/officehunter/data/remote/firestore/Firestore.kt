package com.officehunter.data.remote.firestore

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.User

enum class FirestoreCollection(val id:String) {
    USERS("users"),
    QUESTIONS("questions"),
    OFFICES("offices"),
    HUNTEDS("hunteds"),
    FOUND("found"),
    ANSWERS("answers"),
}

class Firestore {
    private var db = Firebase.firestore

    /*
    fun insert(collection:FirestoreCollection, document: HashMap<String,Any>, onResult: (Result<Unit>) -> Unit){
        db.collection(collection.id).add(document)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { e -> onResult(Result.failure(e))}
    }
    */

    fun upsert(collection: FirestoreCollection, documentId:String?, documentData: Any, onResult: (Result<Unit>) -> Unit){
        if(documentId != null) {
            db.collection(collection.id).document(documentId).set(documentData)
                .addOnSuccessListener { onResult(Result.success(Unit)) }
                .addOnFailureListener { e -> onResult(Result.failure(e))}
        } else {
            db.collection(collection.id).add(documentData)
                .addOnSuccessListener { onResult(Result.success(Unit)) }
                .addOnFailureListener { e -> onResult(Result.failure(e))}
        }
    }

    fun read(collection: FirestoreCollection, onResult: (Result<QuerySnapshot>) -> Unit){
        db.collection(collection.id).get()
            .addOnSuccessListener {onResult(Result.success(it))}
            .addOnFailureListener { e -> onResult(Result.failure(e))}
    }

    fun getHuntedRef(hunted: Hunted):DocumentReference{
        return db.document("${FirestoreCollection.HUNTEDS}/${hunted.id}")
    }

    fun getUserRef(user: User):DocumentReference{
        return db.document("${FirestoreCollection.USERS}/${user.id}")
    }

    companion object {
        const val TAG = "FIRESTORE"
    }
}