package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.model.GameSession
import com.example.minesweeperchess.domain.model.GameStatus
import com.example.minesweeperchess.domain.model.PieceColor
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.GameRepository
import com.example.minesweeperchess.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HistoryItemUiState(
    val gameId: String,
    val dateFormatted: String,
    val creatorName: String,
    val guestName: String,
    val creatorColorIsWhite: Boolean,
    val resultText: String,
    val isFinished: Boolean
)

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val history: List<HistoryItemUiState> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val userId = authRepository.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            gameRepository.getUserGameHistory(userId)
                .onSuccess { sessions ->
                    val uiItems = sessions.map { mapToUiState(it) }
                    _uiState.update { it.copy(isLoading = false, history = uiItems) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private suspend fun mapToUiState(session: GameSession): HistoryItemUiState {
        val creatorProfile = profileRepository.getProfile(session.creatorId, false)
        val guestProfile = session.guestId?.let { profileRepository.getProfile(it, false) }

        val creatorName = creatorProfile?.nickname ?: "Игрок 1"
        val guestName = guestProfile?.nickname ?: "Игрок 2"

        val creatorColorIsWhite = session.creatorColor == PieceColor.WHITE

        val stateStatus = session.state?.status

        val winnerName = when (stateStatus) {
            GameStatus.WHITE_WIN_CHECKMATE -> {
                if (creatorColorIsWhite) creatorName else guestName
            }
            GameStatus.BLACK_WIN_CHECKMATE -> {
                if (!creatorColorIsWhite) creatorName else guestName
            }
            else -> null
        }

        val resultText = when {
            winnerName != null -> "Победа: $winnerName"
            stateStatus == GameStatus.DRAW_STALEMATE -> "Ничья (Пат)"
            stateStatus == GameStatus.PLAYING -> "Игра прервана"
            else -> "Статус неизвестен"
        }

        val isFinished = stateStatus != GameStatus.PLAYING && stateStatus != null

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date(session.createdAt))

        return HistoryItemUiState(
            gameId = session.gameId,
            dateFormatted = dateString,
            creatorName = creatorName,
            guestName = guestName,
            creatorColorIsWhite = creatorColorIsWhite,
            resultText = resultText,
            isFinished = isFinished
        )
    }
}