package com.example.minesweeperchess.domain.model

data class GameSession(
    val gameId: String = "",
    val creatorId: String = "",
    val guestId: String? = null,
    val creatorPresent: Boolean = false,
    val guestPresent: Boolean = false,
    val creatorColor: PieceColor = PieceColor.WHITE,
    val status: String = "LOBBY",
    val createdAt: Long = System.currentTimeMillis(),
    val state: GameState? = null
)