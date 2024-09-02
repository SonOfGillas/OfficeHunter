package com.officehunter.data.remote.firestore.entities

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.entities.getRoleFromName
import com.officehunter.data.entities.getRoleName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.Objects


data class User(
    val id: String,
    val name: String,
    val surname: String,
    val points: Number = 1,
    val coffee: Number = 0,

    /*user info*/
    var hireDateTimestamp: Timestamp? = null,
    var birthdateTimestamp: Timestamp? = null,
    var workRole: WorkRoles? = null,
) {
    val hireDate = hireDateTimestamp?.toDate()
    val birthdate = birthdateTimestamp?.toDate()

    fun toDocument(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "surname" to surname,
            "points" to points,
            "coffee" to coffee,
            "hiredate" to hireDateTimestamp,
            "birthday" to birthdateTimestamp,
            "workrole" to if(workRole != null) getRoleName(workRole!!) else ""
        )
    }

    companion object {
        fun fromQueryDocumentSnapshot(document: QueryDocumentSnapshot): User {
            val data: Map<String, Any> = document.data
            val workRole = data["workrole"] as? String
            return User(
                id = document.id,
                name = data["name"] as? String ?: "unknown",
                surname = data["surname"] as? String ?: "",
                points = data["points"] as? Number ?: 0,
                coffee = data["coffee"] as? Number ?: 0,
                hireDateTimestamp = data["hiredate"] as? Timestamp,
                birthdateTimestamp = data["birthday"] as? Timestamp,
                workRole = getRoleFromName(workRole)
            )
        }
    }
}