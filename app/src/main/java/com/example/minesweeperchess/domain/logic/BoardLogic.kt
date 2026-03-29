package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*

object BoardLogic {

    fun createInitialBoard(size: Int = 8): List<Cell> {
        val cells = mutableListOf<Cell>()

        for (r in 0 until size) {
            for (c in 0 until size) {
                val pos = Position(r, c)
                val piece = getInitialPiece(pos, size)
                cells.add(Cell(position = pos, piece = piece))
            }
        }
        return cells
    }

    private fun getInitialPiece(pos: Position, size: Int): ChessPiece? {
        val color = when (pos.row) {
            0, 1 -> PieceColor.BLACK
            size - 1, size - 2 -> PieceColor.WHITE
            else -> return null
        }

        val type = when (pos.row) {
            1, size - 2 -> PieceType.PAWN
            0, size - 1 -> when (pos.col) {
                0, 7 -> PieceType.ROOK
                1, 6 -> PieceType.KNIGHT
                2, 5 -> PieceType.BISHOP
                3 -> PieceType.QUEEN
                4 -> PieceType.KING
                else -> return null
            }
            else -> return null
        }

        return ChessPiece(type, color)
    }
}