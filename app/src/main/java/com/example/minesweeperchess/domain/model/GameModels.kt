package com.example.minesweeperchess.domain.model

enum class GameMode {
    MINESWEEPER,
    CHESS,
    CHESS_MINESWEEPER
}

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
    val type: PieceType,
    val color: PieceColor,
    val position: Position
)

data class Cell(
    val position: Position,
    val piece: ChessPiece? = null,
    val isMined: Boolean = false,
    val isRevealed: Boolean = false,
    val adjacentMines: Int = 0,
    val isFlagged: Boolean = false
)

data class GameState(
    val mode: GameMode = GameMode.CHESS_MINESWEEPER,
    val board: List<Cell> = emptyList(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val status: GameStatus = GameStatus.PLAYING,
    val boardSize: Int = 8,
    val moveHistory: List<Move> = emptyList()
)

enum class GameStatus {
    PLAYING,
    WHITE_WIN_CHECKMATE,
    BLACK_WIN_CHECKMATE,
    WHITE_WIN_KING_CAPTURED,
    BLACK_WIN_KING_CAPTURED,
    DRAW_STALEMATE,
    MINESWEEPER_WIN,
    MINESWEEPER_LOSS
}