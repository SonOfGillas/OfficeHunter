package com.officehunter.ui.screens.questions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.repositories.UserRepository
import com.officehunter.ui.screens.signUp.SignUpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Date

enum class QuestionStep {
    QUESTIONS_PAGE_1,
    QUESTIONS_PAGE_2,
    QUESTIONS_PAGE_3,
    QUESTIONS_ENDED
}
enum class QuestionsPhase {
    LOADING,
    IDLE,
    GO_NEXT,
    ERROR,
}

data class QuestionsState(
    val questionStep: QuestionStep = QuestionStep.QUESTIONS_PAGE_1,
    val questionsPhase: QuestionsPhase = QuestionsPhase.IDLE,
    val errorMessage: String = "",
    /* QUESTIONS_PAGE_1 */
    val hireDate: Date? = null,
    val workRoleId: Int = 0,
    val residenceCityName: String = "",
    /* QUESTIONS_PAGE_2 */
    val birthDay: Date? = null,
    val bornCityName: String = "",
    val favoriteDish: String = "",
    /* QUESTIONS_PAGE_3 */
    val hobby: String = "",
    val favoriteFilmTvSeries: String = "",
){
    fun hasError():Boolean {
        return questionsPhase == QuestionsPhase.ERROR;
    }
}

interface QuestionsActions {
    fun goNext()
    fun closeError()
    /* QUESTIONS_PAGE_1 */
    fun setHireDate(value: Date)
    fun setWorkRoleId(value: Int)
    fun setResidenceCityName(value: String)
    /* QUESTIONS_PAGE_2 */
    fun setBirthDay(value: Date)
    fun setBornCityName(value: String)
    fun setFavoriteDish(value: String)
    /* QUESTIONS_PAGE_3 */
    fun setHobby(value: String)
    fun setFavoriteFilmTvSeries(value: String)
}
class QuestionsViewModel (
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionsState())
    val state = _state.asStateFlow()

    val actions = object : QuestionsActions {
        override fun goNext() {
           when(state.value.questionStep){
               QuestionStep.QUESTIONS_PAGE_1 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_PAGE_2)}
               QuestionStep.QUESTIONS_PAGE_2 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_PAGE_3)}
               QuestionStep.QUESTIONS_PAGE_3 ->
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_ENDED)}
               else -> {
                   _state.update { it.copy(questionStep = QuestionStep.QUESTIONS_ENDED)}
               }
           }
        }

        override fun closeError(){
            _state.update { it.copy(
                questionsPhase = QuestionsPhase.IDLE,
                errorMessage = ""
            )}
        }

        override fun setHireDate(value: Date) {
            _state.update { it.copy( hireDate = value)}
        }

        override fun setWorkRoleId(value: Int) {
            _state.update { it.copy( workRoleId = value)}
        }

        override fun setResidenceCityName(value: String) {
            _state.update { it.copy( residenceCityName = value)}
        }

        override fun setBirthDay(value: Date) {
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

