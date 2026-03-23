package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkDestination()
    }

    private fun checkDestination() {
        viewModelScope.launch {
            val user = authRepository.currentUser
            if (user == null) {
                _navigationEvent.emit("auth")
            } else {
                val profile = profileRepository.getProfile(user.id)
                if (profile != null && profile.nickname.isNotBlank()) {
                    _navigationEvent.emit("lobby")
                } else {
                    _navigationEvent.emit("profile")
                }
            }
        }
    }
}