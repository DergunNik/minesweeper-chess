package com.example.minesweeperchess.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun MineNumber(count: Int) {
    if (count == 0) return
    val color = when (count) {
        1 -> Color.Blue
        2 -> Color(0xFF388E3C)
        3 -> Color.Red
        4 -> Color(0xFF7B1FA2)
        else -> Color.DarkGray
    }
    Text(text = count.toString(), color = color, fontWeight = FontWeight.Bold)
}