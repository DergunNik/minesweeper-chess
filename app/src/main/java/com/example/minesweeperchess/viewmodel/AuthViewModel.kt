package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.AuthResult
import com.example.minesweeperchess.ui.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var email = MutableStateFlow("")
    var password = MutableStateFlow("")

    fun toggleMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(error = message, isLoading = false) }
    }

    fun authenticate() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val callback: (AuthResult) -> Unit = { result ->
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }

        if (_uiState.value.isLoginMode) {
            repository.signIn(email.value, password.value, callback)
        } else {
            repository.signUp(email.value, password.value, callback)
        }
    }

    fun onGoogleSignInResult(idToken: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        repository.signInWithGoogle(idToken) { result ->
            when (result) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}