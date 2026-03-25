package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*

object GameEvaluator {

    fun evaluate(state: GameState): GameStatus {
        val baseStatus = when (state.mode) {
            GameMode.MINESWEEPER -> evaluateMinesweeper(state)
            GameMode.CHESS -> evaluateChess(state)
            GameMode.CHESS_MINESWEEPER -> evaluateHybrid(state)
        }

        if (baseStatus == GameStatus.PLAYING && state.board.any { it.isMined && it.isRevealed }) {
            return GameStatus.MINESWEEPER_LOSS
        }

        return baseStatus
    }

    private fun evaluateMinesweeper(state: GameState): GameStatus {
        if (state.board.any { it.isMined && it.isRevealed }) {
            return GameStatus.MINESWEEPER_LOSS
        }

        val safeCells = state.board.filter { !it.isMined }
        if (safeCells.all { it.isRevealed }) {
            return GameStatus.MINESWEEPER_WIN
        }

        return GameStatus.PLAYING
    }

    private fun evaluateChess(state: GameState): GameStatus {
        val color = state.currentTurn
        val hasLegalMoves = hasAnyLegalMoves(color, state)
        val isCheck = isKingUnderAttack(color, state.board, state.boardSize)

        return when {
            !hasLegalMoves && isCheck -> if (color == PieceColor.WHITE) GameStatus.BLACK_WIN_CHECKMATE else GameStatus.WHITE_WIN_CHECKMATE
            !hasLegalMoves && !isCheck -> GameStatus.DRAW_STALEMATE
            else -> GameStatus.PLAYING
        }
    }

    private fun evaluateHybrid(state: GameState): GameStatus {
        val whiteKingExists = state.board.any { it.piece?.type == PieceType.KING && it.piece.color == PieceColor.WHITE }
        val blackKingExists = state.board.any { it.piece?.type == PieceType.KING && it.piece.color == PieceColor.BLACK }

        return when {
            !blackKingExists -> GameStatus.WHITE_WIN_KING_CAPTURED
            !whiteKingExists -> GameStatus.BLACK_WIN_KING_CAPTURED
            else -> evaluateChess(state)
        }
    }

    fun isKingUnderAttack(color: PieceColor, board: List<Cell>, size: Int): Boolean {
        val kingPos = board.find { it.piece?.type == PieceType.KING && it.piece.color == color }?.position ?: return false
        val opponentColor = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        return board.filter { it.piece?.color == opponentColor }.any { cell ->
            val piece = cell.piece!!
            MoveValidator.getValidMoves(piece, cell.position, board, size).contains(kingPos)
        }
    }

    private fun hasAnyLegalMoves(color: PieceColor, state: GameState): Boolean {
        val pieceCells = state.board.filter { it.piece?.color == color }
        for (cell in pieceCells) {
            val piece = cell.piece!!
            val currentPos = cell.position
            val potentialMoves = MoveValidator.getValidMoves(piece, currentPos, state.board, state.boardSize)

            for (targetPos in potentialMoves) {
                val tempBoard = simulateMove(state.board, currentPos, targetPos)
                if (!isKingUnderAttack(color, tempBoard, state.boardSize)) {
                    return true
                }
            }
        }
        return false
    }

    private fun simulateMove(board: List<Cell>, from: Position, to: Position): List<Cell> {
        val movingPiece = board.find { it.position == from }?.piece ?: return board
        return board.map { cell ->
            when (cell.position) {
                from -> cell.copy(piece = null)
                to -> cell.copy(piece = movingPiece)
                else -> cell
            }
        }
    }
}