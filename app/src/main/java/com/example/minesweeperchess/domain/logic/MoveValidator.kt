package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*
import kotlin.math.abs

object MoveValidator {

    fun getLegalMoves(piece: ChessPiece, currentPos: Position, state: GameState): List<Position> {
        val pseudoLegalMoves = getPseudoLegalMoves(piece, currentPos, state.board, state.boardSize)
        val validMoves = pseudoLegalMoves.filter { to ->
            val nextBoard = simulateMove(state.board, currentPos, to)
            !GameEvaluator.isKingUnderAttack(piece.color, nextBoard, state.boardSize)
        }.toMutableList()

        if (piece.type == PieceType.KING && !piece.hasMoved && !state.isCheck) {
            validMoves.addAll(getCastlingMoves(piece, currentPos, state))
        }

        return validMoves
    }

    fun getPseudoLegalMoves(piece: ChessPiece, currentPos: Position, board: List<Cell>, size: Int): List<Position> {
        val moves = mutableListOf<Position>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                val target = Position(r, c)
                if (isPseudoLegal(piece, currentPos, target, board)) {
                    moves.add(target)
                }
            }
        }
        return moves
    }

    private fun isPseudoLegal(piece: ChessPiece, start: Position, target: Position, board: List<Cell>): Boolean {
        if (start == target) return false

        val targetCell = board.find { it.position == target }
        if (targetCell?.piece?.color == piece.color) return false

        val dr = target.row - start.row
        val dc = target.col - start.col
        val absDr = abs(dr)
        val absDc = abs(dc)

        return when (piece.type) {
            PieceType.PAWN -> {
                val direction = if (piece.color == PieceColor.WHITE) -1 else 1
                val startRow = if (piece.color == PieceColor.WHITE) 6 else 1

                if (dc == 0 && targetCell?.piece == null) {
                    dr == direction || (start.row == startRow && dr == 2 * direction &&
                            board.find { it.position == Position(start.row + direction, start.col) }?.piece == null)
                } else if (absDc == 1 && dr == direction) {
                    targetCell?.piece != null
                } else false
            }
            PieceType.KNIGHT -> (absDr == 2 && absDc == 1) || (absDr == 1 && absDc == 2)
            PieceType.BISHOP -> absDr == absDc && isPathClear(start, target, board)
            PieceType.ROOK -> (absDr == 0 || absDc == 0) && isPathClear(start, target, board)
            PieceType.QUEEN -> (absDr == absDc || absDr == 0 || absDc == 0) && isPathClear(start, target, board)
            PieceType.KING -> absDr <= 1 && absDc <= 1
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

    private fun getCastlingMoves(king: ChessPiece, kingPos: Position, state: GameState): List<Position> {
        val moves = mutableListOf<Position>()
        val row = kingPos.row

        val kingsideRookPos = Position(row, 7)
        val kingsideRook = state.board.find { it.position == kingsideRookPos }?.piece
        if (kingsideRook?.type == PieceType.ROOK && !kingsideRook.hasMoved) {
            if (isPathClear(kingPos, kingsideRookPos, state.board)) {
                if (!isSquareAttacked(Position(row, 5), king.color, state.board, state.boardSize) &&
                    !isSquareAttacked(Position(row, 6), king.color, state.board, state.boardSize)) {
                    moves.add(Position(row, 6))
                }
            }
        }

        val queensideRookPos = Position(row, 0)
        val queensideRook = state.board.find { it.position == queensideRookPos }?.piece
        if (queensideRook?.type == PieceType.ROOK && !queensideRook.hasMoved) {
            if (isPathClear(kingPos, queensideRookPos, state.board)) {
                if (!isSquareAttacked(Position(row, 3), king.color, state.board, state.boardSize) &&
                    !isSquareAttacked(Position(row, 2), king.color, state.board, state.boardSize)) {
                    moves.add(Position(row, 2))
                }
            }
        }

        return moves
    }

    fun isSquareAttacked(pos: Position, defendingColor: PieceColor, board: List<Cell>, size: Int): Boolean {
        val opponentColor = if (defendingColor == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        return board.filter { it.piece?.color == opponentColor }.any { cell ->
            isPseudoLegal(cell.piece!!, cell.position, pos, board)
        }
    }

    fun simulateMove(board: List<Cell>, from: Position, to: Position): List<Cell> {
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