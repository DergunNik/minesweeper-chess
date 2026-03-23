package com.example.minesweeperchess.ui.state

data class LobbyUiState(
    val isLoading: Boolean = false,
    val gameId: String? = null,
    val error: String? = null,
    val showModeSelection: Boolean = false
)