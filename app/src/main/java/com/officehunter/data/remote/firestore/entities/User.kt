package com.officehunter.data.remote.firestore.entities

import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class User(
    val id: String,
    val name: String,
    val surname: String,
    val points: Number = 0,
    val coffee: Number = 0
) {
    companion object {
        fun fromQueryDocumentSnapshot(document: QueryDocumentSnapshot): User {
            val data: Map<String, Any> = document.data
            return User(
                id = document.id,
                name = data["name"] as? String ?: "unknown",
                surname = data["surname"] as? String ?: "",
                points = data["points"] as? Number ?: 0,
                coffee = data["coffee"] as? Number ?: 0
            )
        }
    }
}