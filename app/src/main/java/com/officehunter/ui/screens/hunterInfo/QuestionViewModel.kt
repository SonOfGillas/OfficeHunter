package com.officehunter.ui.screens.hunterInfo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.entities.getRoleName
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.ImageRepository
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

enum class HunterInfoStep {
    QUESTIONS_PAGE_1,
    QUESTIONS_PAGE_2,
    QUESTIONS_PAGE_3,
    HUNTED_AVATAR,
    QUESTIONS_ENDED
}
enum class HunterInfoPhase {
    LOADING,
    IDLE,
    COMPLETED,
    ERROR,
}

data class HunterInfoState(
    val hunterInfoStep: HunterInfoStep = HunterInfoStep.QUESTIONS_PAGE_1,
    val hunterInfoPhase: HunterInfoPhase = HunterInfoPhase.IDLE,
    val errorMessage: String = "",
    val hireDate: Date? = null,
    val workRole: WorkRoles = WorkRoles.FE_DEV,
    val residenceCityName: String = "",
    val birthDay: Date? = null,
    val bornCityName: String = "",
    val favoriteDish: String = "",
    val hobby: String = "",
    val favoriteFilmTvSeries: String = "",
    val avatarImageUri: Uri = Uri.EMPTY,
){
    fun hasError():Boolean {
        return hunterInfoPhase == HunterInfoPhase.ERROR;
    }
}

interface HunterInfoActions {
    fun goNext()
    fun closeError()
    fun onFinish()
    /* QUESTIONS_PAGE_1 */
    fun setHireDate(value: Date?)
    fun setWorkRole(workRole: WorkRoles)
    fun setResidenceCityName(value: String)
    /* QUESTIONS_PAGE_2 */
    fun setBirthDay(value: Date?)
    fun setBornCityName(value: String)
    fun setFavoriteDish(value: String)
    /* QUESTIONS_PAGE_3 */
    fun setHobby(value: String)
    fun setFavoriteFilmTvSeries(value: String)
    /* Avatar */
    fun setAvatarImageUri(imageUri: Uri)
}
class HunterInfoViewModel (
    private val userRepository: UserRepository,
    private val huntedRepository: HuntedRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HunterInfoState())
    val state = _state.asStateFlow()

    val actions = object : HunterInfoActions {
        override fun goNext() {
           when(state.value.hunterInfoStep){
               /*
               QuestionStep.QUESTIONS_PAGE_1 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_PAGE_2)}
               QuestionStep.QUESTIONS_PAGE_2 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_PAGE_3)}
               QuestionStep.QUESTIONS_PAGE_3 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_ENDED)}
                */
               HunterInfoStep.QUESTIONS_PAGE_1 ->
                   if(state.value.hireDate != null && state.value.birthDay != null){
                       _state.update { it.copy(hunterInfoStep = HunterInfoStep.HUNTED_AVATAR)}
                   } else {
                       _state.update { it.copy(hunterInfoPhase = HunterInfoPhase.ERROR, errorMessage = "Fill all the field") }
                   }
               else -> {
                   _state.update { it.copy(hunterInfoStep = HunterInfoStep.QUESTIONS_PAGE_1)}
               }
           }
        }

        override fun closeError(){
            _state.update { it.copy(
                hunterInfoPhase = HunterInfoPhase.IDLE,
                errorMessage = ""
            )}
        }

        override fun onFinish() {
            viewModelScope.launch {
                _state.update { it.copy(hunterInfoPhase = HunterInfoPhase.LOADING) }
                val avatarImageUri = state.value.avatarImageUri
                val currentUser = userRepository.userRepositoryData.value.currentUser
                val hireDate = state.value.hireDate
                val birthDay =  state.value.birthDay
                if(
                    avatarImageUri != Uri.EMPTY &&
                    currentUser != null &&
                    hireDate != null &&
                    birthDay != null
                    ){
                    userRepository.updateExistingUser(
                        user = currentUser.copy(
                            hireDateTimestamp = Timestamp(hireDate),
                            birthdateTimestamp = Timestamp(birthDay),
                            workRole = state.value.workRole
                            )
                    ){
                        updateExistingUserResut ->
                        updateExistingUserResut.onSuccess {
                            huntedRepository.createNewHunted(currentUser){
                                    result ->
                                result.onSuccess {
                                    it?.let {
                                        imageRepository.addHuntedImage(it.id, avatarImageUri){
                                                imageResult ->
                                            imageResult.onSuccess {
                                                _state.update { hunterInfoState ->
                                                    hunterInfoState.copy(hunterInfoPhase = HunterInfoPhase.COMPLETED) } }
                                                .onFailure { error ->
                                                    _state.update {hunterInfoState ->
                                                        hunterInfoState.copy(
                                                            errorMessage = error.message?:"Unknown Error",
                                                            hunterInfoPhase = HunterInfoPhase.ERROR
                                                        )
                                                    }
                                                }
                                        }
                                    }
                                }.onFailure {
                                    _state.update {
                                        it.copy(
                                            errorMessage = it.errorMessage,
                                            hunterInfoPhase = HunterInfoPhase.ERROR
                                        )
                                    }
                                }
                            }
                        }.onFailure {
                            _state.update {
                                it.copy(
                                    errorMessage = it.errorMessage,
                                    hunterInfoPhase = HunterInfoPhase.ERROR
                                )
                            }
                        }
                    }
                }
            }
        }

        override fun setHireDate(value: Date?) {
            _state.update { it.copy( hireDate = value)}
        }

        override fun setWorkRole(workRole: WorkRoles) {
            _state.update { it.copy( workRole = workRole)}
        }

        override fun setResidenceCityName(value: String) {
            _state.update { it.copy( residenceCityName = value)}
        }

        override fun setBirthDay(value: Date?) {
            _state.update { it.copy( birthDay = value)}
        }

        override fun setBornCityName(value: String) {
            _state.update { it.copy( bornCityName = value)}
        }

        override fun setFavoriteDish(value: String) {
            _state.update { it.copy( favoriteDish = value)}
        }

        override fun setHobby(value: String) {
            _state.update { it.copy( hobby = value)}
        }

        override fun setFavoriteFilmTvSeries(value: String) {
            _state.update { it.copy( favoriteFilmTvSeries = value)}
        }

        override fun setAvatarImageUri(imageUri: Uri) =
            _state.update { it.copy(avatarImageUri = imageUri) }
    }
}

