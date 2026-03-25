package com.example.minesweeperchess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minesweeperchess.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _isOpponentJoined = MutableStateFlow(false)
    val isOpponentJoined: StateFlow<Boolean> = _isOpponentJoined.asStateFlow()

    fun startObserving(gameId: String) {
        viewModelScope.launch {
            repository.observeGame(gameId).collect { game ->
                if (game != null && game.guestId != null && game.status == "PLAYING") {
                    _isOpponentJoined.value = true
                }
            }
        }
    }
}