package com.example.minesweeperchess.ui.state

data class LobbyUiState(
    val isLoading: Boolean = false,
    val gameId: String? = null,
    val isJoined: Boolean = false,
    val error: String? = null,
    val showModeSelection: Boolean = false
)