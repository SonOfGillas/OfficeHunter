package com.officehunter.ui.screens.hunterInfo

import androidx.lifecycle.ViewModel
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

enum class HunterInfoStep {
    QUESTIONS_PAGE_1,
    QUESTIONS_PAGE_2,
    QUESTIONS_PAGE_3,
    HUNTED_IMAGE,
    QUESTIONS_ENDED
}
enum class HunterInfoPhase {
    LOADING,
    IDLE,
    GO_NEXT,
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
){
    fun hasError():Boolean {
        return hunterInfoPhase == HunterInfoPhase.ERROR;
    }
}

interface HunterInfoActions {
    fun goNext()
    fun closeError()
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
}
class HunterInfoViewModel (
    private val repository: UserRepository
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
                   _state.update { it.copy(hunterInfoStep = HunterInfoStep.HUNTED_IMAGE)}
               else -> {
                   _state.update { it.copy(hunterInfoStep = HunterInfoStep.QUESTIONS_ENDED)}
               }
           }
        }

        override fun closeError(){
            _state.update { it.copy(
                hunterInfoPhase = HunterInfoPhase.IDLE,
                errorMessage = ""
            )}
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
    }
}

