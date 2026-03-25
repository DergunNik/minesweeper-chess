package com.example.minesweeperchess.domain.model

data class Move(
    val from: Position = Position(),
    val to: Position = Position(),
    val piece: ChessPiece = ChessPiece(),
    val capturedPiece: ChessPiece? = null,
    val isMineExplosion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val moveNumber: Int = 0
)