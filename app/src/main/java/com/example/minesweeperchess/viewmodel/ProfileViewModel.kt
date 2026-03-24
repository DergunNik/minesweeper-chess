package com.example.minesweeperchess.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.model.User
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.ProfileRepository
import com.example.minesweeperchess.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val defaultAvatars = listOf(
        "https://api.dicebear.com/7.x/bottts/svg?seed=Felix",
        "https://api.dicebear.com/7.x/bottts/svg?seed=Aneka"
    )

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUid = authRepository.currentUser?.id ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = profileRepository.getProfile(currentUid)

            if (user != null) {
                _uiState.update { it.copy(
                    nickname = user.nickname,
                    avatarUrl = user.avatarUrl,
                    isLoading = false
                ) }
            } else {
                val authName = authRepository.currentUser?.nickname ?: ""
                _uiState.update { it.copy(
                    nickname = authName,
                    isLoading = false
                ) }
            }
        }
    }

    fun onNicknameChange(newName: String) {
        _uiState.update { it.copy(nickname = newName) }
    }

    fun selectDefaultAvatar(url: String) {
        _uiState.update { it.copy(avatarUrl = url, selectedUri = null) }
    }

    fun selectCustomImage(uri: Uri) {
        _uiState.update { it.copy(selectedUri = uri, avatarUrl = "") }
    }

    fun saveProfile() {
        val currentUid = authRepository.currentUser?.id ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val finalAvatarUrl = if (_uiState.value.selectedUri != null) {
                val uploadResult = profileRepository.uploadAvatar(currentUid, _uiState.value.selectedUri!!)
                uploadResult.getOrNull() ?: _uiState.value.avatarUrl
            } else {
                _uiState.value.avatarUrl
            }

            if (finalAvatarUrl.isEmpty() && _uiState.value.selectedUri != null) {
                _uiState.update { it.copy(isLoading = false, error = "Ошибка загрузки фото") }
                return@launch
            }

            val profile = User(
                id = currentUid,
                nickname = _uiState.value.nickname,
                avatarUrl = finalAvatarUrl.ifEmpty { defaultAvatars[0] },
                email = authRepository.currentUser?.email ?: ""
            )

            val result = profileRepository.saveProfile(profile)
            _uiState.update { it.copy(isLoading = false, isSaved = result.isSuccess) }
        }
    }
}