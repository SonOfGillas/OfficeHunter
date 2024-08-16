package com.officehunter.data.remote.firestore.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
data class Found (
    private val foundTimestamp: Timestamp?,
    val huntedRef:  DocumentReference?,
    val userRef:  DocumentReference?,
) {
    val foundDate = foundTimestamp?.toDate()
    companion object {
        fun fromQueryDocumentSnapshot(document: QueryDocumentSnapshot): Found {
            val data: Map<String, Any> = document.data
            return Found(
                foundTimestamp = data["foundDate"] as? Timestamp ,
                huntedRef = data["huntedRef"] as? DocumentReference,
                userRef = data["userRef"] as? DocumentReference
            )
        }
    }
}