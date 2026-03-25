package com.example.minesweeperchess.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.logic.GameActions
import com.example.minesweeperchess.domain.logic.MoveValidator
import com.example.minesweeperchess.domain.model.*
import com.example.minesweeperchess.domain.repository.GameRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _selectedPosition = MutableStateFlow<Position?>(null)
    val selectedPosition: StateFlow<Position?> = _selectedPosition.asStateFlow()

    private val _validMoves = MutableStateFlow<List<Position>>(emptyList())
    val validMoves: StateFlow<List<Position>> = _validMoves.asStateFlow()

    private var currentGameId: String? = null
    private val currentUserId = auth.currentUser?.uid

    fun initGame(gameId: String) {
        currentGameId = gameId
        viewModelScope.launch {
            repository.observeGame(gameId).collect { session ->
                session?.state?.let { newState ->
                    _gameState.value = newState
                }
            }
        }
    }

    fun onCellClick(position: Position) {
        val state = _gameState.value
        if (state.status != GameStatus.PLAYING) return

        val cell = state.board.find { it.position == position }
        val selected = _selectedPosition.value

        when (state.mode) {
            GameMode.MINESWEEPER -> {
                updateGameState(GameActions.processCellClick(state, position))
            }
            else -> {
                if (selected == null) {
                    if (cell?.piece != null && cell.piece.color == state.currentTurn) {
                        _selectedPosition.value = position
                        _validMoves.value = MoveValidator.getLegalMoves(cell.piece, position, state)                    }
                } else {
                    if (_validMoves.value.contains(position)) {
                        val newState = GameActions.makeMove(state, selected, position)
                        updateGameState(newState)
                    }
                    _selectedPosition.value = null
                    _validMoves.value = emptyList()
                }
            }
        }
    }

    fun onCellLongClick(position: Position) {
        if (_gameState.value.mode == GameMode.MINESWEEPER) {
            updateGameState(GameActions.toggleFlag(_gameState.value, position))
        }
    }

    private fun updateGameState(newState: GameState) {
        val gameId = currentGameId ?: return
        viewModelScope.launch {
            try {
                firestore.collection("games").document(gameId)
                    .update("state", newState)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}