package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.entities.Found
import com.officehunter.data.remote.firestore.entities.Hunted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class HuntedRepositoryData(
    val huntedList: List<Hunted> = emptyList<Hunted>()
)
class HuntedRepository(
    private val firestore: Firestore,
    private val userRepository: UserRepository,
) {
    val huntedRepositoryData = MutableStateFlow<HuntedRepositoryData>(HuntedRepositoryData())

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
    }

}