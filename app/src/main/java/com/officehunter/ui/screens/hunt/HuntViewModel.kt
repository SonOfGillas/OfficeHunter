package com.officehunter.ui.screens.hunt

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.R
import com.officehunter.data.database.Achievement
import com.officehunter.data.database.Office
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.AchievementRepository
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.ImageRepository
import com.officehunter.data.repositories.NearestOffice
import com.officehunter.data.repositories.OfficesRepository
import com.officehunter.data.repositories.UserRepository
import com.officehunter.data.repositories.defaultOffices
import com.officehunter.ui.composables.map.MarkerInfo
import com.officehunter.utils.Answer
import com.officehunter.utils.Coordinates
import com.officehunter.utils.Question
import com.officehunter.utils.getRandomQuestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
    val markerInfos: List<MarkerInfo> = emptyList(),

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false,
    val userPosition: GeoPoint? = null,
    val nearestOffice: NearestOffice? = null,
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
    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun  setUserPosition(coordinates: Coordinates)
}

class HuntViewModel(
    private val huntedRepository: HuntedRepository,
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository,
    private val imageRepository: ImageRepository,
    private val officesRepository: OfficesRepository,
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

    /*
    private val offices: StateFlow<List<Office>> = officesRepository.offices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = defaultOffices
    )
     */

    private val officesMarkers = defaultOffices.map {
        MarkerInfo(
            GeoPoint(it.latitude,it.longitude),
            icon = R.drawable.offices_primary
        )
    }

    private var spawnJob: Job? = null
    private var updateSpawnLoopOnHuntedDataChangeJob: Job? = null
    private var updateSpawnLoopOnLastSpawnTimeChangeJob: Job? = null

    private fun getRandomCoordinate(startingCoordinate:Double):Double{
        val randomAmount = Random.nextInt(0,1000)
        if(randomAmount%2==0){
            return startingCoordinate + (Random.nextInt(0,200) * 0.00001)
        } else {
            return startingCoordinate - (Random.nextInt(0,200) * 0.00001)
        }
    }

    private fun spawnHunted(){
        val nearestOffice = state.value.nearestOffice
        if(nearestOffice != null && nearestOffice.distanceKm <= MAX_SPAWN_DISTANCE_KM){
            val currentTime = Date()
            val timeDifference = currentTime.time - lastSpawnTime.value.time
            val weightedHuntedList = huntData.value.weightedHuntedList
            if (timeDifference > SPAWN_PERIOD && weightedHuntedList.isNotEmpty()) {
                val randomIndex = Random.nextInt(weightedHuntedList.size)
                val latitude = getRandomCoordinate(nearestOffice.office.latitude)
                val longitude = getRandomCoordinate(nearestOffice.office.longitude)
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
    }

    private fun startSpawnLoop(){
        spawnJob = viewModelScope.launch {
            while (true) {
                spawnHunted()
                updateMarkerInfos()
                Log.d(TAG, "spawnedHunted: ${state.value.markerInfos.size}")
                delay(SPAWN_PERIOD)
            }
        }
    }

    private fun stopSpawnLoop(){
        spawnJob?.cancel()
    }

    private fun getQuestion() {
        val huntedOwner = state.value.selectedHunted?.owner
        if (huntedOwner != null){
            _state.update { it.copy(question = getRandomQuestion(huntedOwner)) }
        }
    }

    private fun updateMarkerInfos(){
        val huntedMarkers = state.value.spawnedHunted.map {
            MarkerInfo(
                it.position,
                icon = R.drawable.logov2_shadow,
                onClick = {actions.hunt(it.hunted)}
            )
        }
        if(state.value.userPosition != null){
            val userMarker = listOf(MarkerInfo(
                state.value.userPosition!!,
            ))
            _state.update { it.copy(markerInfos = userMarker+officesMarkers+huntedMarkers ) }
        }

    }

    private fun getNearestOffice(){
        if(state.value.userPosition!=null){
            viewModelScope.launch{
                val nearestOffice = officesRepository.getNearestOffice(state.value.userPosition!!)
                _state.update { it.copy(nearestOffice=nearestOffice) }
                Log.d("NearestOffice","${nearestOffice?.office?.name}")
            }
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
            startSpawnLoop()
            updateSpawnLoopOnHuntedDataChangeJob = viewModelScope.launch {
                huntData.collect{
                    Log.d("spawnLoopControl","huntData Changed")
                    stopSpawnLoop()
                    startSpawnLoop()
                }
            }
            updateSpawnLoopOnLastSpawnTimeChangeJob = viewModelScope.launch {
                lastSpawnTime.collect {
                    Log.d("spawnLoopControl","lastSpawnTime Changed")
                    stopSpawnLoop()
                    startSpawnLoop()
                }

            }
        }

        override fun stopSpawning() {
            spawnJob?.cancel()
            updateSpawnLoopOnHuntedDataChangeJob?.cancel()
            updateSpawnLoopOnLastSpawnTimeChangeJob?.cancel()
        }

        override fun setShowLocationDisabledAlert(show: Boolean) =
            _state.update { it.copy(showLocationDisabledAlert = show) }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) =
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) =
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }

        override fun setUserPosition(coordinates: Coordinates) {
           _state.update { it.copy(userPosition= GeoPoint(coordinates.latitude,coordinates.longitude),status = HuntStatus.IDLE) }
            updateMarkerInfos()
            getNearestOffice()
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
                        actions.checkAnyNewAchievement()
                    }
                }
            }
            result.onFailure {
                it.message?.let { it1 -> Log.d(TAG, it1) }
            }
        }
    }

    companion object{
        const val TAG = "HuntViewModel"
        val SPAWN_PERIOD = TimeUnit.SECONDS.toMillis(10)
        val MAX_SPAWN_DISTANCE_KM = 1.0
    }
}