package com.example.pcraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcraft.data.model.AuthUser
import com.example.pcraft.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser

    val isConfigured: Boolean
        get() = authRepository.isConfigured

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun setRegisterMode(value: Boolean) {
        _isRegisterMode.value = value
        _errorMessage.value = null
    }

    fun submit() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        if (emailValue.isBlank() || passwordValue.isBlank()) {
            _errorMessage.value = "Введите email и пароль."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = if (_isRegisterMode.value) {
                authRepository.signUp(emailValue, passwordValue)
            } else {
                authRepository.signIn(emailValue, passwordValue)
            }

            _isLoading.value = false

            result.onSuccess {
                _password.value = ""
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Не удалось выполнить вход."
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _password.value = ""
        _errorMessage.value = null
    }
}

