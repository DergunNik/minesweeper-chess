package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*

object BoardLogic {

    fun createInitialBoard(mode: GameMode, size: Int = 8, mineCount: Int = 10): List<Cell> {
        val cells = mutableListOf<Cell>()

        val minePositions = mutableSetOf<Position>()
        if (mode == GameMode.MINESWEEPER || mode == GameMode.CHESS_MINESWEEPER) {
            while (minePositions.size < mineCount) {
                val r = (0 until size).random()
                val c = (0 until size).random()
                if (mode == GameMode.CHESS_MINESWEEPER && (r < 2 || r > 5)) continue
                minePositions.add(Position(r, c))
            }
        }

        for (r in 0 until size) {
            for (c in 0 until size) {
                val pos = Position(r, c)
                val isMined = minePositions.contains(pos)
                val piece = if (mode != GameMode.MINESWEEPER) getInitialPiece(pos, size) else null

                cells.add(Cell(
                    position = pos,
                    piece = piece,
                    isMined = isMined
                ))
            }
        }

        return calculateAdjacentMines(cells, size)
    }

    private fun getInitialPiece(pos: Position, size: Int): ChessPiece? {
        val color = when (pos.row) {
            0, 1 -> PieceColor.WHITE
            size - 1, size - 2 -> PieceColor.BLACK
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

    private fun calculateAdjacentMines(board: List<Cell>, size: Int): List<Cell> {
        return board.map { cell ->
            if (cell.isMined) cell
            else {
                val count = getNeighbors(cell.position, size).count { pos ->
                    board.find { it.position == pos }?.isMined == true
                }
                cell.copy(adjacentMines = count)
            }
        }
    }

    fun getNeighbors(pos: Position, size: Int): List<Position> {
        val neighbors = mutableListOf<Position>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = pos.row + dr
                val nc = pos.col + dc
                if (nr in 0 until size && nc in 0 until size) {
                    neighbors.add(Position(nr, nc))
                }
            }
        }
        return neighbors
    }
}