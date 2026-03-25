package com.example.minesweeperchess.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.minesweeperchess.domain.model.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinesweeperBoard(
    state: GameState,
    onCellClick: (Position) -> Unit,
    onCellLongClick: (Position) -> Unit,
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

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .background(if (cell.isRevealed) Color.LightGray else Color.Gray)
                                .border(0.5.dp, Color.DarkGray)
                                .combinedClickable(
                                    onClick = { onCellClick(pos) },
                                    onLongClick = { onCellLongClick(pos) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.isRevealed) {
                                if (cell.isMined) Text("💣")
                                else MineNumber(cell.adjacentMines)
                            } else if (cell.isFlagged) {
                                Text("🚩")
                            }
                        }
                    }
                }
            }
        }
    }
}