package com.officehunter.ui.screens.hunt

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.HuntedRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.util.GeoPoint
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class SpawnedHunted(
    val hunted: Hunted,
    val position: GeoPoint
)

data class HuntState(
    val spawnedHunted: List<SpawnedHunted> = emptyList()
)

data class HuntData(
    val huntedList: List<Hunted> = emptyList(),
    val weightedHuntedList: List<Hunted> = emptyList()
)

interface HuntActions{

}
class HuntViewModel(
    private val huntedRepository: HuntedRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HuntState())
    val state = _state.asStateFlow()
    private val lastSpawnTime = huntedRepository.lastSpawnTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Date(0)
    )
    private val huntData: StateFlow<HuntData> = huntedRepository.huntedRepositoryData.map{
        HuntData(
            huntedList = it.huntedList,
            weightedHuntedList = it.huntedList.flatMap { hunted ->
                List(hunted.extractionWeight) { hunted.copy() }
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HuntData()
    )

    val userPosition = GeoPoint(44.148357, 12.235488)

    private fun getRandomCoordinate(startingCoordinate:Double):Double{
        val randomAmount = Random.nextInt(0,1000)
        if(randomAmount%2==0){
            return startingCoordinate + (Random.nextInt(0,200) * 0.00001)
        } else {
            return startingCoordinate - (Random.nextInt(0,200) * 0.00001)
        }
    }

    val actions = object : HuntActions {
    }

    init {
        val TAG = "HuntViewModel"

        huntedRepository.updateData {
                result ->
            result.onSuccess {
                Log.d(TAG, "hunted list ${huntedRepository.huntedRepositoryData.value.huntedList}")
            }
            result.onFailure {
                it.message?.let { it1 -> Log.d("HuntViewModel", it1) }
            }
        }

        viewModelScope.launch {
            lastSpawnTime.collect {
                huntData.collect{
                    while (true) {
                        val currentTime = Date()
                        val timeDifference = currentTime.time - lastSpawnTime.value.time
                        val weightedHuntedList = huntData.value.weightedHuntedList
                        if (timeDifference > SPAWN_PERIOD && weightedHuntedList.isNotEmpty()) {
                            val randomIndex = Random.nextInt(weightedHuntedList.size)
                            val latitude = getRandomCoordinate(44.1483)
                            val longitude = getRandomCoordinate(12.2354)
                            Log.d(TAG, latitude.toString())
                            val randomHunted = SpawnedHunted(
                                weightedHuntedList[randomIndex],
                                GeoPoint(latitude, longitude)
                            )
                            runBlocking {
                                huntedRepository.setLastSpawnTime()
                            }
                            if(state.value.spawnedHunted.size >= 5){
                                val currentList = _state.value.spawnedHunted
                                _state.value = _state.value.copy(
                                    spawnedHunted = currentList.subList(1,currentList.size) + randomHunted
                                )
                            } else {
                                _state.value = _state.value.copy(
                                    spawnedHunted = _state.value.spawnedHunted + randomHunted
                                )
                            }
                        }
                        delay(SPAWN_PERIOD) // Check every second
                        Log.d(TAG, state.value.spawnedHunted.size.toString())
                    }
                }
            }
        }
    }

    companion object{
        val SPAWN_PERIOD = TimeUnit.SECONDS.toMillis(5)
    }
}