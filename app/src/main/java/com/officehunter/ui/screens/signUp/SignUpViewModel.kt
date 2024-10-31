package com.officehunter.ui.screens.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SignUpPhase {
    IDLE,
    LOADING,
    SIGN_UP,
    ERROR,
}

data class SignUpState(
    val phase: SignUpPhase = SignUpPhase.IDLE,
    val errorMessage: String? = null,
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val passwordCopy: String = ""
){
    val canSubmit get() = name.isNotBlank() &&
            surname.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            passwordCopy.isNotBlank() &&
            password == passwordCopy

    fun hasError(): Boolean{
        return phase == SignUpPhase.ERROR
    }
}

interface  SignUpActions {
    fun setName(value:String)
    fun setSurname(value: String)
    fun setEmail(value: String)
    fun setPassword(value: String)
    fun setPasswordCopy(value: String)
    fun signUp()
}

class SignUpViewModel (
    private val repository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    val actions = object : SignUpActions {
        override fun setName(value:String){
            _state.update { it.copy(name = value) }
        }

        override fun setSurname(value: String){
            _state.update { it.copy(surname = value) }
        }

        override fun setEmail(value: String) {
            _state.update { it.copy(email = value) }
            // viewModelScope.launch { repository.setUsername(value) }
        }

        override fun setPassword(value: String){
            _state.update { it.copy(password = value) }
        }

        override fun setPasswordCopy(value: String){
            _state.update { it.copy(passwordCopy = value) }
        }

        override fun signUp(){
            if(_state.value.canSubmit){
                _state.update { it.copy(phase = SignUpPhase.LOADING) }
                viewModelScope.launch{
                    repository.signUp(
                        name = _state.value.name,
                        surname = _state.value.surname,
                        email = _state.value.email,
                        password = _state.value.password
                    ){
                        result ->
                            result
                                .onSuccess { _state.update { it.copy(phase = SignUpPhase.SIGN_UP) } }
                                .onFailure { _state.update { it.copy(phase = SignUpPhase.ERROR, errorMessage = it.errorMessage) }
                            _state.update { it.copy(phase = SignUpPhase.IDLE, errorMessage = null) }
                    }
                    }
                }
            }
        }
    }
}

