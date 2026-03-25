package com.example.minesweeperchess.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.minesweeperchess.domain.model.*

@Composable
fun BoardComponent(
    state: GameState,
    selectedPosition: Position?,
    validMoves: List<Position>,
    onCellClick: (Position) -> Unit,
    onCellLongClick: (Position) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (state.mode) {
        GameMode.MINESWEEPER -> {
            MinesweeperBoard(
                state = state,
                onCellClick = onCellClick,
                onCellLongClick = onCellLongClick,
                modifier = modifier
            )
        }
        GameMode.CHESS -> {
            ChessBoard(
                state = state,
                selectedPosition = selectedPosition,
                validMoves = validMoves,
                onCellClick = onCellClick,
                modifier = modifier
            )
        }
        GameMode.CHESS_MINESWEEPER -> {
            HybridBoard(
                state = state,
                selectedPosition = selectedPosition,
                validMoves = validMoves,
                onCellClick = onCellClick,
                modifier = modifier
            )
        }
    }
}