package com.example.minesweeperchess.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.minesweeperchess.domain.model.*

@Composable
fun ChessBoard(
    state: GameState,
    selectedPosition: Position?,
    validMoves: List<Position>,
    onCellClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        val cellSize = maxWidth / state.boardSize
        Column {
            for (row in 0 until state.boardSize) {
                Row {
                    for (col in 0 until state.boardSize) {
                        val pos = Position(row, col)
                        val cell = state.board.find { it.position == pos } ?: Cell(pos)
                        val isDark = (row + col) % 2 != 0
                        val isSelected = pos == selectedPosition
                        val isValidMove = validMoves.contains(pos)

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(
                                    when {
                                        isSelected -> Color.Yellow.copy(alpha = 0.5f)
                                        isValidMove -> Color.Green.copy(alpha = 0.3f)
                                        isDark -> Color(0xFF769656)
                                        else -> Color(0xFFEEEED2)
                                    }
                                )
                                .border(if (isValidMove) 2.dp else 0.dp, Color.Green)
                                .clickable { onCellClick(pos) },
                            contentAlignment = Alignment.Center
                        ) {
                            PieceLabel(cell.piece)
                        }
                    }
                }
            }
        }
    }
}