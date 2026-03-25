package com.example.minesweeperchess.domain.repository

import com.example.minesweeperchess.domain.model.GameSession
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun createGame(userId: String): Result<String>
    suspend fun joinGame(gameId: String, userId: String): Result<Unit>
    suspend fun leaveGame(gameId: String)
    fun observeGame(gameId: String): Flow<GameSession?>
}