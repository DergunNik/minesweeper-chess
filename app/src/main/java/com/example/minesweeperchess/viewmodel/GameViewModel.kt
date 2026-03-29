package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.logic.GameActions
import com.example.minesweeperchess.domain.logic.GameEvaluator
import com.example.minesweeperchess.domain.logic.MoveValidator
import com.example.minesweeperchess.domain.model.*
import com.example.minesweeperchess.domain.repository.GameRepository
import com.example.minesweeperchess.domain.repository.ProfileRepository
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
    private val profileRepository: ProfileRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameSession = MutableStateFlow<GameSession?>(null)
    val gameSession: StateFlow<GameSession?> = _gameSession.asStateFlow()

    private val _selectedPosition = MutableStateFlow<Position?>(null)
    val selectedPosition: StateFlow<Position?> = _selectedPosition.asStateFlow()

    private val _validMoves = MutableStateFlow<List<Position>>(emptyList())
    val validMoves: StateFlow<List<Position>> = _validMoves.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    private val _opponentProfile = MutableStateFlow<User?>(null)
    val opponentProfile: StateFlow<User?> = _opponentProfile.asStateFlow()

    private var currentGameId: String? = null
    val currentUserId = auth.currentUser?.uid

    fun getPlayerColor(): PieceColor? {
        val session = _gameSession.value ?: return null
        return if (currentUserId == session.creatorId) {
            session.creatorColor
        } else {
            if (session.creatorColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        }
    }

    fun initGame(gameId: String) {
        currentGameId = gameId
        viewModelScope.launch {
            repository.observeGame(gameId).collect { session ->
                _gameSession.value = session
                session?.state?.let { newState ->
                    _gameState.value = GameEvaluator.validateAndUpdate(newState)
                }

                session?.let { s ->
                    val myId = currentUserId

                    if (myId != null && _currentUserProfile.value == null) {
                        launch {
                            _currentUserProfile.value = profileRepository.getProfile(myId, false)
                        }
                    }

                    val opponentId = if (myId == s.creatorId) s.guestId else s.creatorId
                    if (opponentId != null && _opponentProfile.value == null) {
                        launch {
                            _opponentProfile.value = profileRepository.getProfile(opponentId, false)
                        }
                    }
                }
            }
        }
    }

    fun onCellClick(position: Position) {
        val state = _gameState.value
        val playerColor = getPlayerColor()

        if (state.status != GameStatus.PLAYING || state.currentTurn != playerColor) return

        val cell = state.board.find { it.position == position }
        val selected = _selectedPosition.value

        if (selected == null) {
            if (cell?.piece != null && cell.piece.color == playerColor) {
                _selectedPosition.value = position
                _validMoves.value = MoveValidator.getLegalMoves(cell.piece, position, state)
            }
        } else {
            if (_validMoves.value.contains(position)) {
                var newState = GameActions.makeMove(state, selected, position)
                newState = GameEvaluator.validateAndUpdate(newState)
                updateGameState(newState)
            }
            _selectedPosition.value = null
            _validMoves.value = emptyList()
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

    fun leaveGame() {
        val gameId = currentGameId ?: return
        viewModelScope.launch {
            repository.leaveGame(gameId)
        }
    }
}