package com.example.minesweeperchess.domain.model

data class GameSession(
    val gameId: String = "",
    val creatorId: String = "",
    val guestId: String? = null,
    val status: String = "LOBBY",
    val createdAt: Long = System.currentTimeMillis()
)