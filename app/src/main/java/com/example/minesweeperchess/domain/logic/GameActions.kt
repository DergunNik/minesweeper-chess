package com.example.minesweeperchess.domain.logic

import com.example.minesweeperchess.domain.model.*

object GameActions {

    fun processCellClick(state: GameState, clickedPos: Position): GameState {
        val newState = when (state.mode) {
            GameMode.MINESWEEPER -> revealCell(state, clickedPos)
            else -> state
        }

        return newState.copy(status = GameEvaluator.evaluate(newState))
    }

    private fun revealCell(state: GameState, pos: Position): GameState {
        val cell = state.board.find { it.position == pos } ?: return state
        if (cell.isRevealed || cell.isFlagged || state.status != GameStatus.PLAYING) return state

        if (cell.isMined) {
            return state.copy(
                board = state.board.map { it.copy(isRevealed = true) },
                status = GameStatus.MINESWEEPER_LOSS
            )
        }

        val newBoard = state.board.toMutableList()
        floodFill(newBoard, pos, state.boardSize)

        return state.copy(board = newBoard)
    }

    private fun floodFill(board: MutableList<Cell>, pos: Position, size: Int) {
        val index = board.indexOfFirst { it.position == pos }
        if (index == -1 || board[index].isRevealed) return

        board[index] = board[index].copy(isRevealed = true)

        if (board[index].adjacentMines == 0) {
            BoardLogic.getNeighbors(pos, size).forEach { neighbor ->
                floodFill(board, neighbor, size)
            }
        }
    }

    fun makeMove(state: GameState, from: Position, to: Position): GameState {
        if (state.status != GameStatus.PLAYING) return state

        val fromCell = state.board.find { it.position == from } ?: return state
        val movingPiece = fromCell.piece ?: return state
        val targetCell = state.board.find { it.position == to } ?: return state

        val isExplosion = state.mode == GameMode.CHESS_MINESWEEPER && targetCell.isMined
        val captured = targetCell.piece

        val newMove = Move(
            from = from,
            to = to,
            piece = movingPiece,
            capturedPiece = captured,
            isMineExplosion = isExplosion,
            moveNumber = state.moveHistory.size + 1
        )

        val newBoard = state.board.map { cell ->
            when (cell.position) {
                from -> cell.copy(piece = null)
                to -> {
                    if (isExplosion) {
                        cell.copy(piece = null, isRevealed = true)
                    } else {
                        cell.copy(piece = movingPiece.copy(position = to), isRevealed = true)
                    }
                }
                else -> cell
            }
        }

        val nextTurn = if (state.currentTurn == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE

        val intermediateState = state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            moveHistory = state.moveHistory + newMove
        )

        return intermediateState.copy(status = GameEvaluator.evaluate(intermediateState))
    }

    fun toggleFlag(state: GameState, pos: Position): GameState {
        if (state.mode != GameMode.MINESWEEPER || state.status != GameStatus.PLAYING) return state

        val newBoard = state.board.map { cell ->
            if (cell.position == pos && !cell.isRevealed) {
                cell.copy(isFlagged = !cell.isFlagged)
            } else cell
        }
        return state.copy(board = newBoard)
    }
}