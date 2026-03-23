package com.example.minesweeperchess.ui.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoginMode: Boolean = true
)
