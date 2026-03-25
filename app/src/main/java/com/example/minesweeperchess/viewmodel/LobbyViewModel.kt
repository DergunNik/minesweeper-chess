package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.logic.BoardLogic
import com.example.minesweeperchess.domain.model.GameMode
import com.example.minesweeperchess.domain.model.GameState
import com.example.minesweeperchess.domain.model.GameStatus
import com.example.minesweeperchess.domain.model.PieceColor
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.GameRepository
import com.example.minesweeperchess.ui.state.LobbyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleModeSelection(show: Boolean) {
        _uiState.update { it.copy(showModeSelection = show) }
    }

    fun startPvPGame(selectedMode: GameMode) {
        val userId = authRepository.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val initialBoard = BoardLogic.createInitialBoard(selectedMode)
            val initialState = GameState(
                mode = selectedMode,
                board = initialBoard,
                currentTurn = PieceColor.WHITE,
                status = GameStatus.PLAYING
            )

            val result = gameRepository.createGame(userId, initialState)

            result.onSuccess { id ->
                _uiState.update { it.copy(isLoading = false, gameId = id, showModeSelection = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun joinGame(gameId: String) {
        val userId = authRepository.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = gameRepository.joinGame(gameId, userId)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, gameId = gameId, isJoined = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}