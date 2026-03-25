package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*
import kotlin.math.abs

object MoveValidator {

    fun getValidMoves(piece: ChessPiece, currentPos: Position, board: List<Cell>, size: Int): List<Position> {
        val moves = mutableListOf<Position>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                val target = Position(r, c)
                if (isValidChessMove(piece, currentPos, target, board)) {
                    moves.add(target)
                }
            }
        }
        return moves
    }

    private fun isValidChessMove(piece: ChessPiece, currentPos: Position, target: Position, board: List<Cell>): Boolean {
        if (currentPos == target) return false

        val targetCell = board.find { it.position == target }
        if (targetCell?.piece?.color == piece.color) return false

        val dr = abs(target.row - currentPos.row)
        val dc = abs(target.col - currentPos.col)

        return when (piece.type) {
            PieceType.PAWN -> {
                val direction = if (piece.color == PieceColor.WHITE) 1 else -1
                val startRow = if (piece.color == PieceColor.WHITE) 1 else 6

                if (dc == 0 && targetCell?.piece == null) {
                    (target.row - currentPos.row) == direction ||
                            (currentPos.row == startRow && (target.row - currentPos.row) == 2 * direction &&
                                    isPathClear(currentPos, target, board))
                } else if (dc == 1 && (target.row - currentPos.row) == direction) {
                    targetCell?.piece != null
                } else false
            }
            PieceType.KNIGHT -> (dr == 2 && dc == 1) || (dr == 1 && dc == 2)
            PieceType.BISHOP -> dr == dc && isPathClear(currentPos, target, board)
            PieceType.ROOK -> (dr == 0 || dc == 0) && isPathClear(currentPos, target, board)
            PieceType.QUEEN -> (dr == dc || dr == 0 || dc == 0) && isPathClear(currentPos, target, board)
            PieceType.KING -> dr <= 1 && dc <= 1
        }
    }

    private fun isPathClear(start: Position, end: Position, board: List<Cell>): Boolean {
        val dr = (end.row - start.row).coerceIn(-1, 1)
        val dc = (end.col - start.col).coerceIn(-1, 1)
        var currR = start.row + dr
        var currC = start.col + dc

        while (currR != end.row || currC != end.col) {
            if (board.find { it.position == Position(currR, currC) }?.piece != null) return false
            currR += dr
            currC += dc
        }
        return true
    }

    fun getLegalMoves(piece: ChessPiece, currentPos: Position, state: GameState): List<Position> {
        val pseudoLegalMoves = getValidMoves(piece, currentPos, state.board, state.boardSize)
        return pseudoLegalMoves.filter { to ->
            val nextBoard = simulateMove(state.board, currentPos, to)
            !GameEvaluator.isKingUnderAttack(piece.color, nextBoard, state.boardSize)
        }
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