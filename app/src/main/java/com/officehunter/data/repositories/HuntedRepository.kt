package com.officehunter.data.repositories

import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.entities.Found
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date

data class HuntedRepositoryData(
    val huntedList: List<Hunted> = emptyList<Hunted>()
)
class HuntedRepository(
    private val firestore: Firestore,
    private val userRepository: UserRepository,
    private val dataStore: DataStore<Preferences>,
) {
    val huntedRepositoryData = MutableStateFlow<HuntedRepositoryData>(HuntedRepositoryData())

    /* Last Time an Hunted is Spawned */
    val lastSpawnTime: Flow<Date> = dataStore.data.map { preferences ->
        val timestamp:Long = preferences[LAST_SPAWN]?.toString()?.toLong() ?: 0
        Date(timestamp)
    }
    suspend fun setLastSpawnTime(){
        val timestamp: Long = System.currentTimeMillis()
        dataStore.edit { it[LAST_SPAWN] = timestamp.toString()}
    }

    fun huntedFounded(hunted: Hunted){
        val currentUser = userRepository.userRepositoryData.value.currentUser
        if(currentUser!=null){
            val found = Found(
                foundTimestamp = Timestamp.now(),
                huntedRef = firestore.getHuntedRef(hunted),
                userRef = firestore.getUserRef(currentUser)
            )
            firestore.upsert(
                collection = FirestoreCollection.FOUND,
                documentId = null,
                documentData = found
            ){}
        }
    }

    fun createNewHunted(user: User, onResult: (Result<DocumentReference?>) -> Unit){
        val newHunted =  mapOf(
            "name" to user.name,
            "surname" to user.surname,
            "rank" to 1,
            "variant" to "Original",
            "userRef" to firestore.getUserRef(user)
        )
        firestore.upsert(
            collection = FirestoreCollection.HUNTEDS,
            documentId = null,
            documentData = newHunted
        ) { result ->
            onResult(result)
        }
    }


    fun updateData(onResult: (Result<Unit>) -> Unit){
        /* Points of Users are need to calculate the rarity of the hunteds */
        userRepository.updateData{
            result ->  result.onSuccess {
                Log.d(TAG,"user updated")
                getHuntedList(onResult)
            }.onFailure{ onResult(Result.failure(it)) }
        }
    }

    private fun getHuntedList(onResult: (Result<Unit>) -> Unit){
        firestore.read(FirestoreCollection.HUNTEDS){
                result ->
            result.onSuccess {
                Log.d(TAG,"success: read Hunted collection")
                val currentHuntedList = it.map { document -> Hunted.fromQueryDocumentSnapshot(document) }
                Log.d(TAG,currentHuntedList.toString())
                getFoundingList(currentHuntedList, onResult)
            }.onFailure{ onResult(Result.failure(it)) }
        }
    }

    private fun getFoundingList(currentHuntedList:List<Hunted>, onResult: (Result<Unit>) -> Unit){
        /* found is the collection of all founded hunted*/
        firestore.read(FirestoreCollection.FOUND){
                result ->
            result.onSuccess {
                Log.d(TAG,"success: read Found collection")
                val foundList = it.map { document -> Found.fromQueryDocumentSnapshot(document) }
                Log.d(TAG,foundList.toString())
                updateHuntedData(currentHuntedList,foundList,onResult)
            }.onFailure{ onResult(Result.failure(it)) }
        }
    }

    private fun updateHuntedData(currentHuntedList:List<Hunted>, foundingList: List<Found>, onResult: (Result<Unit>) -> Unit){
        try {
            updateHuntedWeightAndRarity(currentHuntedList,foundingList)
        } catch (e:Exception){
            e.message?.let { Log.d(TAG, it) }
            onResult(Result.failure(e))
        }
        updateSpawnRate(currentHuntedList)
        Log.d(TAG,currentHuntedList.toString())
        huntedRepositoryData.update { data -> data.copy(huntedList = currentHuntedList) }
        onResult(Result.success(Unit))
    }

    private fun updateHuntedWeightAndRarity(currentHuntedList:List<Hunted>, foundingList: List<Found>){
        val userList = userRepository.userRepositoryData.value.usersList
        val currentUser = userRepository.userRepositoryData.value.currentUser
        for(hunted in currentHuntedList){
            val owner = userList.first { user -> hunted.isOwner(user) }
            hunted.updateWeight(owner)
            hunted.updateRarity(userList.size,foundingList,currentUser)
        }
    }

    private fun updateSpawnRate(currentHuntedList:List<Hunted>){
        //TODO this should be done by office, ex totalWaitByOffice[BolognaOfficeId]
        val totalWeight = currentHuntedList.fold(0){acc, hunted -> acc+hunted.weight}
        var totalExtractionWeight = 0
        for(hunted in currentHuntedList){
            hunted.updateExtractionWeight(totalWeight)
            totalExtractionWeight+=hunted.extractionWeight
        }
        for(hunted in currentHuntedList){
           hunted.updateSpawnRate(totalExtractionWeight)
        }
    }

    companion object {
        val TAG = "HuntedRepository"
        private val LAST_SPAWN = stringPreferencesKey("last_spawn")
    }

}