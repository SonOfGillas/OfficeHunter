package com.officehunter.ui.screens.hunt

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.R
import com.officehunter.data.database.Achievement
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.AchievementRepository
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.ImageRepository
import com.officehunter.data.repositories.UserRepository
import com.officehunter.ui.composables.map.MarkerInfo
import com.officehunter.utils.Answer
import com.officehunter.utils.Question
import com.officehunter.utils.getRandomQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

enum class HuntStatus{
    SETUP,
    LOADING,
    IDLE,
}

data class HuntState(
    val status:HuntStatus = HuntStatus.SETUP,
    val spawnedHunted: List<SpawnedHunted> = emptyList(),
    val selectedHunted: Hunted? = null,
    val question: Question? = null,
    val achievementsToShow: List<Achievement> = emptyList(),
    val errorMessage: String? = null,
    val markerInfos: List<MarkerInfo> = emptyList()
){
    fun hasError():Boolean{
        return errorMessage != null
    }
}

data class HuntData(
    val huntedList: List<Hunted> = emptyList(),
    val weightedHuntedList: List<Hunted> = emptyList()
)

interface HuntActions{
    fun hunt(hunted: Hunted)
    fun closeHunt()
    fun closeAchievement()
    suspend fun  getHuntedImage(hunted: Hunted):Uri?
    fun onAnsware(answer: Answer)
    suspend fun getAchievementsIcon(imageName: String): Uri?
    fun checkAnyNewAchievement()
    fun startSpawningHunted()
    fun  resetError()
    fun stopSpawning()
}

class HuntViewModel(
    private val huntedRepository: HuntedRepository,
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository,
    private val imageRepository: ImageRepository,
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
    private var spawnJob: Job? = null

    private fun getRandomCoordinate(startingCoordinate:Double):Double{
        val randomAmount = Random.nextInt(0,1000)
        if(randomAmount%2==0){
            return startingCoordinate + (Random.nextInt(0,200) * 0.00001)
        } else {
            return startingCoordinate - (Random.nextInt(0,200) * 0.00001)
        }
    }

    private fun spawnHunted(){
        val currentTime = Date()
        val timeDifference = currentTime.time - lastSpawnTime.value.time
        val weightedHuntedList = huntData.value.weightedHuntedList
        if (timeDifference > SPAWN_PERIOD && weightedHuntedList.isNotEmpty()) {
            val randomIndex = Random.nextInt(weightedHuntedList.size)
            val latitude = getRandomCoordinate(44.1483)
            val longitude = getRandomCoordinate(12.2354)
            val randomHunted = SpawnedHunted(
                weightedHuntedList[randomIndex],
                GeoPoint(latitude, longitude)
            )
            _state.value = _state.value.copy(
                spawnedHunted = _state.value.spawnedHunted + randomHunted
            )
            runBlocking {
                huntedRepository.setLastSpawnTime()
            }
        }
    }



    fun getQuestion() {
        val huntedOwner = state.value.selectedHunted?.owner
        if (huntedOwner != null){
            _state.update { it.copy(question = getRandomQuestion(huntedOwner)) }
        }
    }

    val actions = object : HuntActions {
        override fun hunt(hunted: Hunted) {
            _state.update { it.copy(selectedHunted = hunted) }
            getQuestion()
        }

        override fun closeHunt() {
            _state.update { it.copy(selectedHunted = null) }
        }

        override fun closeAchievement() {
            _state.update { it.copy(achievementsToShow = it.achievementsToShow.subList(1,it.achievementsToShow.size)) }
        }

        override suspend fun getHuntedImage(hunted: Hunted): Uri? {
            return imageRepository.getHuntedImage(hunted.id)
        }

        override fun onAnsware(answer: Answer) {
            viewModelScope.launch {
                state.value.selectedHunted?.let { hunted ->
                    if (answer.isCorretAnsware){
                        achievementRepository.getHuntedAchievement(hunted){
                            result -> result.onSuccess {
                                achievement -> _state.update { it.copy(achievementsToShow = it.achievementsToShow + achievement)  }
                                userRepository.updateUserPoints(listOf(achievement)){
                                    it.onSuccess {
                                        if(!hunted.isFoundedByCurrentUser()){
                                            huntedRepository.huntedFounded(hunted)
                                        }
                                        huntedRepository.updateData {}
                                        userRepository.updateData {}
                                    }
                                }
                            }
                        }
                    } else {
                        _state.update { it.copy(errorMessage = "Wrong Answer") }
                    }
                }
            }
            this.closeHunt()
        }

        override suspend fun getAchievementsIcon(imageName: String): Uri? {
            return imageRepository.getAchievementImage(imageName)
        }

        override fun checkAnyNewAchievement(){
            viewModelScope.launch {
                val hireDate =  userRepository.userRepositoryData.value.currentUser?.hireDate
                if(hireDate != null){
                    achievementRepository.getHireDateAchievement(hireDate){
                            result ->  result.onSuccess { newAchievements ->
                        userRepository.updateUserPoints(newAchievements){
                            result -> result.onSuccess {
                                    huntedRepository.updateData {}
                                    userRepository.updateData {}
                                    _state.update { it.copy(achievementsToShow = it.achievementsToShow + newAchievements)  }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun startSpawningHunted() {
            spawnJob = viewModelScope.launch {
                while (true) {
                    spawnHunted()
                    _state.update {
                        it.copy(
                            markerInfos = it.spawnedHunted.map {hunted ->
                                MarkerInfo(
                                    hunted.position,
                                    icon = R.drawable.logov2_shadow,
                                    onClick = {hunt(hunted.hunted)}
                                )
                            }
                        )
                    }
                    Log.d(TAG, "spawnedHunted: ${state.value.markerInfos.size}")
                    delay(SPAWN_PERIOD)
                }
            }
        }

        override fun stopSpawning() {
            spawnJob?.cancel()
        }

        override fun resetError() {
            _state.update { it.copy(errorMessage = null) }
        }

    }

    init {
        Log.d("SpawnHunted","init")
        huntedRepository.updateData {
                result ->
            result.onSuccess {
                Log.d(TAG, "hunted list ${huntedRepository.huntedRepositoryData.value.huntedList}")
                userRepository.updateData {
                        result -> result.onSuccess {
                        _state.update { it.copy(status = HuntStatus.IDLE) }
                        actions.checkAnyNewAchievement()
                    }
                }
            }
            result.onFailure {
                it.message?.let { it1 -> Log.d(TAG, it1) }
            }
        }

        viewModelScope.launch {
            lastSpawnTime.collect {
                Log.d("collectTest","lastSpawnTime Changeed")
                actions.stopSpawning()
                actions.startSpawningHunted()
            }

        }

        viewModelScope.launch{
            huntData.collect{
                Log.d("collectTest","huntData Changeed")
                actions.stopSpawning()
                actions.startSpawningHunted()
            }
        }

        /* Spawn first Hunted
            viewModelScope.launch {
                /*
                lastSpawnTime.collect {
                    huntData.collect{
                        spawnHunted()
                    }
                }
                 */
            }

            viewModelScope.launch {
                /*
                lastSpawnTime.collect {
                    huntData.collect{
                        while (true) {
                            delay(SPAWN_PERIOD)
                            spawnHunted()
                            Log.d(TAG, state.value.spawnedHunted.size.toString())
                        }
                    }
                }

                 */
            }
             */
    }

    companion object{
        const val TAG = "HuntViewModel"
        val SPAWN_PERIOD = TimeUnit.SECONDS.toMillis(5)
    }
}