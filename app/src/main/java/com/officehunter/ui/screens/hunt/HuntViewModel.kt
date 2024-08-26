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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class HuntState(
    val spawnedHunted: List<Hunted> = emptyList()
)

data class HuntData(
    val lastSpawnTime:Date = Date(0),
    val huntedList: List<Hunted> = emptyList(),
    val weightedHuntedList: List<Hunted> = emptyList()
)

interface HuntActions{

}
class HuntViewModel(
    private val huntedRepository: HuntedRepository,
) : ViewModel() {

    private val lastSpawnTime = huntedRepository.lastSpawnTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Date(0)
    )

    private val _state = MutableStateFlow(HuntState())
    val state = _state.asStateFlow()

    /*
    private val weightedHuntedList = huntedRepository.huntedRepositoryData.map {
        it.huntedList.flatMap { hunted ->
            List(hunted.extractionWeight) { hunted.copy() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList<Hunted>()
    )*/

    private val huntData: StateFlow<HuntData> = combine(
        huntedRepository.huntedRepositoryData,
        huntedRepository.lastSpawnTime
    ){ huntedData, lastSpawnTime ->
        HuntData(
            lastSpawnTime,
            huntedData.huntedList,
            huntedData.huntedList.flatMap { hunted ->
                List(hunted.extractionWeight) { hunted.copy() }
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HuntData()
    )


    private fun spawnHunter(huntData:StateFlow<HuntData>){
        val currentTime = Date()
        val timeDifference = currentTime.time - lastSpawnTime.value.time
        val weightedHuntedList = huntedRepository.huntedRepositoryData.value.huntedList
       // Log.d("HuntViewModel", "lastSpawnTime.value.time ${lastSpawnTime.value.time}")
       // Log.d("HuntViewModel", "weightedList size ${weightedHuntedList.size}")
        if (timeDifference > SPAWN_PERIOD && weightedHuntedList.isNotEmpty()) {
            val randomIndex = Random.nextInt(weightedHuntedList.size)
            val randomHunted = weightedHuntedList[randomIndex]
         //   Log.d("HuntViewModel", "randomHunted ${randomHunted.surname}")
            runBlocking {
                huntedRepository.setLastSpawnTime()
            }
            /*
            if(state.value.spawnedHunted.size >= 10){
                val currentList = _state.value.spawnedHunted
                _state.value = _state.value.copy(
                    spawnedHunted = currentList.subList(1,currentList.size) + randomHunted
                )
            } else {
                _state.value = _state.value.copy(
                    spawnedHunted = _state.value.spawnedHunted + randomHunted
                )
            }

             */
        }
    }

    val actions = object : HuntActions {
    }

    init {
        huntedRepository.updateData {
                result ->
            result.onSuccess {
                Log.d("HuntViewModel", "hunted list ${huntedRepository.huntedRepositoryData.value.huntedList}")
            }
            result.onFailure {
                it.message?.let { it1 -> Log.d("HuntViewModel", it1) }
            }
        }

        /*
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                spawnHunter(huntData)
                mainHandler.postDelayed(this, SPAWN_PERIOD)
            }
        })
         */

         // Coroutine to check time difference and move a random element
        viewModelScope.launch {
            lastSpawnTime.collect { lastSpawn ->
                while (true) {
                    val currentTime = Date()
                    val timeDifference = currentTime.time - lastSpawn.time
                    Log.d("HuntViewModel", "lastSpawnTime.value.time ${lastSpawnTime.value.time}")
                    runBlocking {
                        huntedRepository.setLastSpawnTime()
                    }
                    delay(1000) // Check every second
                }
            }
        }
    }

    companion object{
        val SPAWN_PERIOD = TimeUnit.SECONDS.toMillis(5)
    }
}