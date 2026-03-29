package com.example.minesweeperchess.data.repository

import com.example.minesweeperchess.domain.model.GameSession
import com.example.minesweeperchess.domain.model.GameState
import com.example.minesweeperchess.domain.model.PieceColor
import com.example.minesweeperchess.domain.repository.GameRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : GameRepository {

    private val gamesCollection = firestore.collection("games")

    override suspend fun createGame(
        userId: String,
        initialState: GameState,
        creatorColor: PieceColor
    ): Result<String> = try {
        var finalGameId: String? = null

        for (attempt in 1..5) {
            val tempId = (100000..999999).random().toString()
            val doc = gamesCollection.document(tempId).get().await()

            if (!doc.exists()) {
                val newGame = GameSession(
                    gameId = tempId,
                    creatorId = userId,
                    creatorPresent = true,
                    guestPresent = false,
                    creatorColor = creatorColor,
                    status = "LOBBY",
//                    createdAt = System.currentTimeMillis(), // todo: check
                    state = initialState
                )
                gamesCollection.document(tempId).set(newGame).await()
                finalGameId = tempId
                break
            }
        }

        if (finalGameId != null) {
            Result.success(finalGameId)
        } else {
            Result.failure(Exception("Не удалось найти свободный ID"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun joinGame(gameId: String, userId: String): Result<Unit> = try {
        val snapshot = gamesCollection.document(gameId).get().await()

        if (snapshot.exists()) {
            val game = snapshot.toObject(GameSession::class.java)
            if (game == null) {
                Result.failure(Exception("Ошибка обработки данных"))
            } else if (game.creatorId == userId) {
                gamesCollection.document(gameId).update("creatorPresent", true).await()
                Result.success(Unit)
            } else if (game.guestId == userId) {
                gamesCollection.document(gameId).update("guestPresent", true).await()
                Result.success(Unit)
            } else if (game.guestId != null) {
                Result.failure(Exception("Игра уже заполнена"))
            } else {
                gamesCollection.document(gameId).update(
                    "guestId", userId,
                    "guestPresent", true,
                    "status", "PLAYING"
                ).await()
                Result.success(Unit)
            }
        } else {
            Result.failure(Exception("Игра не найдена"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun leaveGame(gameId: String) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val snapshot = gamesCollection.document(gameId).get().await()
            if (snapshot.exists()) {
                val game = snapshot.toObject(GameSession::class.java) ?: return

                // Обновляем статус присутствия
                if (game.creatorId == userId) {
                    gamesCollection.document(gameId).update("creatorPresent", false).await()
                } else if (game.guestId == userId) {
                    gamesCollection.document(gameId).update("guestPresent", false).await()
                }

                val updatedSnapshot = gamesCollection.document(gameId).get().await()
                val updatedGame = updatedSnapshot.toObject(GameSession::class.java)

                // Удаляем игру ТОЛЬКО если оба вышли И игра так и не началась
                // (никто не присоединился или нет ходов)
                if (updatedGame != null && !updatedGame.creatorPresent && !updatedGame.guestPresent) {
                    val isGameEmpty = updatedGame.guestId == null || updatedGame.state?.moveHistory?.isEmpty() == true
                    if (isGameEmpty) {
                        gamesCollection.document(gameId).delete().await()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeGame(gameId: String): Flow<GameSession?> {
        return gamesCollection.document(gameId).snapshots().map { snapshot ->
            snapshot.toObject(GameSession::class.java)
        }
    }

    override suspend fun getUserGameHistory(userId: String): Result<List<GameSession>> = try {
        val creatorGames = gamesCollection.whereEqualTo("creatorId", userId).get().await()
        val guestGames = gamesCollection.whereEqualTo("guestId", userId).get().await()

        val allGames = (creatorGames.documents + guestGames.documents)
            .distinctBy { it.id }
            .mapNotNull { it.toObject(GameSession::class.java) }
            .filter { it.guestId != null }
            .sortedByDescending { it.createdAt }

        Result.success(allGames)
    } catch (e: Exception) {
        Result.failure(e)
    }
}