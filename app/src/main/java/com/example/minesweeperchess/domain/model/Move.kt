package com.example.minesweeperchess.domain.model

data class Move(
    val from: Position,
    val to: Position,
    val piece: ChessPiece,
    val capturedPiece: ChessPiece? = null,
    val isMineExplosion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val moveNumber: Int = 0
)