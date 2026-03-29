package com.example.minesweeperchess.domain.repository

import com.example.minesweeperchess.domain.model.GameSession
import com.example.minesweeperchess.domain.model.GameState
import com.example.minesweeperchess.domain.model.PieceColor
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun createGame(userId: String, initialState: GameState, creatorColor: PieceColor): Result<String>    suspend fun joinGame(gameId: String, userId: String): Result<Unit>
    suspend fun leaveGame(gameId: String)
    fun observeGame(gameId: String): Flow<GameSession?>
}