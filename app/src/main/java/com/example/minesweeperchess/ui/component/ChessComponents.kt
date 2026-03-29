package com.example.minesweeperchess.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minesweeperchess.domain.model.*

@Composable
fun PieceLabel(piece: ChessPiece?) {
    if (piece == null) return
    val color = if (piece.color == PieceColor.WHITE) Color.White else Color.Black
    val shadowColor = if (piece.color == PieceColor.WHITE) Color.Black else Color.White

    val symbol = when (piece.type) {
        PieceType.PAWN -> "♙"
        PieceType.KNIGHT -> "♘"
        PieceType.BISHOP -> "♗"
        PieceType.ROOK -> "♖"
        PieceType.QUEEN -> "♕"
        PieceType.KING -> "♔"
    }

    Text(
        text = symbol,
        fontSize = 32.sp,
        color = color,
        style = LocalTextStyle.current.copy(
            shadow = androidx.compose.ui.graphics.Shadow(shadowColor, blurRadius = 2f)
        )
    )
}

@Composable
fun ChessBoard(
    state: GameState,
    selectedPosition: Position?,
    validMoves: List<Position>,
    onCellClick: (Position) -> Unit,
    playerColor: PieceColor?,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        val cellSize = maxWidth / state.boardSize
        val shouldFlip = playerColor == PieceColor.BLACK
        val rows = if (shouldFlip) (state.boardSize - 1 downTo 0) else (0 until state.boardSize)
        val cols = if (shouldFlip) (state.boardSize - 1 downTo 0) else (0 until state.boardSize)

        Column {
            for (row in rows) {
                Row {
                    for (col in cols) {
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