package com.example.minesweeperchess.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.minesweeperchess.domain.model.*

@Composable
fun HybridBoard(
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
                                        cell.isRevealed && cell.isMined -> Color.Red.copy(alpha = 0.7f)
                                        isSelected -> Color.Yellow.copy(alpha = 0.5f)
                                        isDark -> Color(0xFF769656)
                                        else -> Color(0xFFEEEED2)
                                    }
                                )
                                .border(if (isValidMove) 2.dp else 0.5.dp, if (isValidMove) Color.Cyan else Color.Black.copy(0.1f))
                                .clickable { onCellClick(pos) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.isRevealed && !cell.isMined) {
                                MineNumber(cell.adjacentMines)
                            }

                            PieceLabel(cell.piece)

                            if (isValidMove) {
                                Box(
                                    modifier = Modifier
                                        .size(cellSize / 3)
                                        .background(Color.Black.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}