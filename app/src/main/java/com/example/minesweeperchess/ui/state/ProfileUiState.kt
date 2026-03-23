package com.example.minesweeperchess.ui.state

import android.net.Uri

data class ProfileUiState(
    val nickname: String = "",
    val avatarUrl: String = "",
    val selectedUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)