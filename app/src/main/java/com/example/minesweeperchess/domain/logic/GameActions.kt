package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*
import kotlin.math.abs

object GameActions {

    fun makeMove(state: GameState, from: Position, to: Position): GameState {
        if (state.status != GameStatus.PLAYING) return state

        val fromCell = state.board.find { it.position == from } ?: return state
        val movingPiece = fromCell.piece ?: return state
        if (movingPiece.color != state.currentTurn) return state

        val targetCell = state.board.find { it.position == to } ?: return state
        val captured = targetCell.piece

        val isCastling = movingPiece.type == PieceType.KING && abs(from.col - to.col) == 2

        var newBoard = state.board.map { cell ->
            when (cell.position) {
                from -> cell.copy(piece = null)
                to -> cell.copy(piece = movingPiece.copy(hasMoved = true))
                else -> cell
            }
        }

        if (isCastling) {
            val rookFromCol = if (to.col == 6) 7 else 0
            val rookToCol = if (to.col == 6) 5 else 3
            val rookFromPos = Position(from.row, rookFromCol)
            val rookToPos = Position(from.row, rookToCol)

            val rook = newBoard.find { it.position == rookFromPos }?.piece

            newBoard = newBoard.map { cell ->
                when (cell.position) {
                    rookFromPos -> cell.copy(piece = null)
                    rookToPos -> cell.copy(piece = rook?.copy(hasMoved = true))
                    else -> cell
                }
            }
        }

        val newMove = Move(
            from = from,
            to = to,
            piece = movingPiece,
            capturedPiece = captured,
            isCastling = isCastling,
            moveNumber = state.moveHistory.size + 1
        )

        val nextTurn = if (state.currentTurn == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        val isNextPlayerInCheck = GameEvaluator.isKingUnderAttack(nextTurn, newBoard, state.boardSize)

        val intermediateState = state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            moveHistory = state.moveHistory + newMove,
            isCheck = isNextPlayerInCheck
        )

        return intermediateState.copy(status = GameEvaluator.evaluate(intermediateState))
    }
}