package com.example.minesweeperchess.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.minesweeperchess.domain.model.*
import com.example.minesweeperchess.ui.component.ChessBoard
import com.example.minesweeperchess.viewmodel.GameViewModel

@Composable
fun GameScreen(
    gameId: String,
    onQuit: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSession by viewModel.gameSession.collectAsState()
    val selectedPosition by viewModel.selectedPosition.collectAsState()
    val validMoves by viewModel.validMoves.collectAsState()
    val playerColor = viewModel.getPlayerColor()

    val currentUserProfile by viewModel.currentUserProfile.collectAsState()
    val opponentProfile by viewModel.opponentProfile.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.initGame(gameId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val isOpponentAway = gameSession?.let {
            if (viewModel.currentUserId == it.creatorId) !it.guestPresent else !it.creatorPresent
        } ?: false

        if (isOpponentAway) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Соперник отключился. Ожидаем возвращения...",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        PlayerInfoRow(
            isOpponent = true,
            nickname = opponentProfile?.nickname ?: "Соперник",
            avatarUrl = opponentProfile?.avatarUrl,
            playerColor = playerColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        GameHeader(gameId = gameId, state = gameState)

        Spacer(modifier = Modifier.height(8.dp))

        ChessBoard(
            state = gameState,
            selectedPosition = selectedPosition,
            validMoves = validMoves,
            onCellClick = { pos -> viewModel.onCellClick(pos) },
            playerColor = playerColor,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        PlayerInfoRow(
            isOpponent = false,
            nickname = "Вы (${currentUserProfile?.nickname ?: ""})",
            avatarUrl = currentUserProfile?.avatarUrl,
            playerColor = playerColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoveHistoryList(
            moves = gameState.moveHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.leaveGame()
                onQuit()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth(0.6f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Выйти из игры")
        }
    }
}

@Composable
fun PlayerInfoRow(
    isOpponent: Boolean,
    nickname: String,
    avatarUrl: String?,
    playerColor: PieceColor?
) {
    val colorText = if (isOpponent) {
        if (playerColor == PieceColor.WHITE) "⚫ Черные" else "⚪ Белые"
    } else {
        if (playerColor == PieceColor.WHITE) "⚪ Белые" else "⚫ Черные"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nickname.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(nickname, fontWeight = FontWeight.Bold)
                Text(colorText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun GameHeader(gameId: String, state: GameState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "ID: $gameId", style = MaterialTheme.typography.labelMedium)

            val statusText = when (state.status) {
                GameStatus.PLAYING -> {
                    val turn = if (state.currentTurn == PieceColor.WHITE) "Белых" else "Черных"
                    if (state.isCheck) "ШАХ! Ход $turn" else "Ход $turn"
                }
                GameStatus.WHITE_WIN_CHECKMATE -> "Мат! Белые победили"
                GameStatus.BLACK_WIN_CHECKMATE -> "Мат! Черные победили"
                GameStatus.DRAW_STALEMATE -> "Ничья (Пат)"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = when {
                    state.status != GameStatus.PLAYING -> MaterialTheme.colorScheme.error
                    state.isCheck -> Color(0xFFD32F2F)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun MoveHistoryList(moves: List<Move>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        LazyColumn(
            modifier = Modifier.padding(8.dp),
            reverseLayout = true
        ) {
            items(moves.reversed()) { move ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val castlingText = if (move.isCastling) " (Рокировка)" else ""
                    Text(
                        text = "${move.moveNumber}. ${move.piece.type} (${move.from.row},${move.from.col}) → (${move.to.row},${move.to.col})$castlingText",
                        fontSize = 14.sp
                    )
                    if (move.capturedPiece != null) {
                        Text(text = "⚔️ Взятие", color = Color(0xFFE91E63), fontSize = 12.sp)
                    }
                }
                HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
            }
        }
    }
}