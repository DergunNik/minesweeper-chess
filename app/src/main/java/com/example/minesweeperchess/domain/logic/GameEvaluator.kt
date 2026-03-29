package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*

object GameEvaluator {

    fun evaluate(state: GameState): GameStatus {
        val color = state.currentTurn
        val hasLegalMoves = hasAnyLegalMoves(color, state)

        return when {
            !hasLegalMoves && state.isCheck -> if (color == PieceColor.WHITE) GameStatus.BLACK_WIN_CHECKMATE else GameStatus.WHITE_WIN_CHECKMATE
            !hasLegalMoves && !state.isCheck -> GameStatus.DRAW_STALEMATE
            else -> GameStatus.PLAYING
        }
    }

    fun isKingUnderAttack(color: PieceColor, board: List<Cell>, size: Int): Boolean {
        val kingPos = board.find { it.piece?.type == PieceType.KING && it.piece.color == color }?.position ?: return false
        return MoveValidator.isSquareAttacked(kingPos, color, board, size)
    }

    private fun hasAnyLegalMoves(color: PieceColor, state: GameState): Boolean {
        val pieceCells = state.board.filter { it.piece?.color == color }
        for (cell in pieceCells) {
            val piece = cell.piece!!
            val validMoves = MoveValidator.getLegalMoves(piece, cell.position, state)
            if (validMoves.isNotEmpty()) {
                return true
            }
        }
        return false
    }
}