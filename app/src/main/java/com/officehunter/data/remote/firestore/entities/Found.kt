package com.officehunter.data.remote.firestore.entities

import com.google.firebase.firestore.QueryDocumentSnapshot
import java.sql.Timestamp

data class Found (
    val foundDate: String,
    val huntedRef: String,
    val userRef: String,
) {
    companion object {
        fun fromQueryDocumentSnapshot(document: QueryDocumentSnapshot): Found {
            val data: Map<String, Any> = document.data
            return Found(
                foundDate = "",
                huntedRef = "",
                userRef = ""
            )
        }
    }
}