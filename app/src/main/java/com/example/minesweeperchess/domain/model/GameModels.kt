package com.example.minesweeperchess.domain.model

enum class PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}

enum class PieceColor {
    WHITE, BLACK
}

data class Position(
    val row: Int = 0,
    val col: Int = 0
)

data class ChessPiece(
    val type: PieceType = PieceType.PAWN,
    val color: PieceColor = PieceColor.WHITE,
    val hasMoved: Boolean = false
)

data class Cell(
    val position: Position = Position(0, 0),
    val piece: ChessPiece? = null
)

data class Move(
    val from: Position = Position(),
    val to: Position = Position(),
    val piece: ChessPiece = ChessPiece(),
    val capturedPiece: ChessPiece? = null,
    val isCastling: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val moveNumber: Int = 0
)

data class GameState(
    val board: List<Cell> = emptyList(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val status: GameStatus = GameStatus.PLAYING,
    val isCheck: Boolean = false,
    val boardSize: Int = 8,
    val moveHistory: List<Move> = emptyList()
)

enum class GameStatus {
    PLAYING,
    WHITE_WIN_CHECKMATE,
    BLACK_WIN_CHECKMATE,
    DRAW_STALEMATE
}