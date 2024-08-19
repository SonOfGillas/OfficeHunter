package com.officehunter.data.remote.firestore.entities

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.officehunter.data.remote.firestore.FirestoreCollection
import io.ktor.util.reflect.typeInfo
import java.util.Date

enum class Rarity(val formattedName: String) {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    VERY_RARE("very rare"),
    ULTRA_RARE("ultra rare"),
    EPIC("epic"),
    LEGENDARY("legendary"),
    UNDISCOVERED("undiscovered")
}
data class Hunted(
    val id: String,
    val name: String,
    val surname: String,
    /*
        rank is the creation order of Hunted for an hunter,
        higher is the rank, lower will be the spawn rate
    */
    val rank: Number,
    val variant: String,
    val userRef: DocumentReference?,
    /* wight of an hunted is Higher if it has a High Rank and the Hunter have High Points */
    var weight: Int = 0,
    /* rarity is a name to idetify how many people found the Hunted, is related to the found rate*/
    var rarity: Rarity = Rarity.COMMON,
    var foundRate: Double = 0.0,
    /* foundDate of the currentUser */
    var foundDate:  Date? = null,
    /* spawnRate = SumOfAllExtractionWeight/extractionWeight */
    var extractionWeight: Int = 0,
    var spawnRate: Double = 0.0,
){
    val isFoundedByCurrentUser = foundDate != null

    fun updateWeight(owner: User){
        this.weight = this.rank.toInt()*owner.points.toInt()
    }

    fun updateRarity(usersNumber:Int,founds:List<Found>,currentUser: User?){
      var timesFounded=0.0
        for(found in founds){
            if(found.huntedRef?.path.toString() == "${FirestoreCollection.HUNTEDS.id}/${id}"){
                timesFounded++
                if(found.userRef?.path.toString() == "${FirestoreCollection.USERS.id}/${currentUser?.id}"){
                    foundDate=found.foundDate
                }
            }
        }
      this.foundRate = timesFounded/usersNumber
      when {
          foundRate > 0.64 -> rarity = Rarity.COMMON
          (foundRate <= 0.64 && foundRate > 0.32) -> rarity = Rarity.UNCOMMON
          (foundRate <= 0.32 && foundRate > 0.16) -> rarity = Rarity.RARE
          (foundRate <= 0.16 && foundRate > 0.08) -> rarity = Rarity.VERY_RARE
          (foundRate <= 0.08 && foundRate > 0.04) -> rarity = Rarity.ULTRA_RARE
          (foundRate <= 0.04 && foundRate > 0.02) -> rarity = Rarity.EPIC
          (foundRate <= 0.02 && foundRate > 0.0) -> rarity = Rarity.LEGENDARY
          foundRate == 0.0 -> rarity = Rarity.UNDISCOVERED
      }
    }

    //total weight of hunted in that office
    fun updateExtractionWeight(totalWeight: Int){
        this.extractionWeight = totalWeight/this.weight
    }

    //total totalExtractionWeight of hunted in that office
    fun updateSpawnRate(totalExtractionWeight: Int){
        this.spawnRate = extractionWeight.toDouble() / totalExtractionWeight.toDouble()
    }

    fun  isOwner(user: User):Boolean{
        return userRef?.path.toString() == "${FirestoreCollection.USERS.id}/${user.id}"
    }

    companion object {
        val TAG = "HuntedObject"
        fun fromQueryDocumentSnapshot(document: QueryDocumentSnapshot): Hunted {
            val data: Map<String, Any> = document.data
            return Hunted(
                id = document.id,
                name = data["name"] as? String ?: "unknown",
                surname = data["surname"] as? String ?: "",
                rank = data["rank"] as? Number ?: 1,
                variant= data["variant"] as? String ?: "unknown",
                userRef = data["userRef"] as? DocumentReference
            )
        }
    }
}