package com.example.minesweeperchess.data.repository

import com.example.minesweeperchess.domain.model.GameSession
import com.example.minesweeperchess.domain.repository.GameRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : GameRepository {

    private val gamesCollection = firestore.collection("games")

    override suspend fun createGame(userId: String): Result<String> = try {
        var finalGameId: String? = null

        for (attempt in 1..5) {
            val tempId = (100000..999999).random().toString()
            val doc = gamesCollection.document(tempId).get().await()

            if (!doc.exists()) {
                val newGame = GameSession(gameId = tempId, creatorId = userId, status = "LOBBY")
                gamesCollection.document(tempId).set(newGame).await()
                finalGameId = tempId
                break
            }
        }

        if (finalGameId != null) {
            Result.success(finalGameId)
        } else {
            Result.failure(Exception("Не удалось найти свободный ID за 5 попыток"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun joinGame(gameId: String, userId: String): Result<Unit> = try {
        val snapshot = gamesCollection.document(gameId).get().await()

        if (snapshot.exists()) {
            val game = snapshot.toObject(GameSession::class.java)
            if (game == null) {
                Result.failure(Exception("Внутренняя ошибка при обработке игры"))
            }
            else if (game.guestId != null) {
                Result.failure(Exception("Игра уже заполнена"))
            }
            else if (game.creatorId == userId) {
                Result.failure(Exception("Вы создатель"))
            }
            else {
                gamesCollection.document(gameId).update(
                    "guestId", userId,
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
            gamesCollection.document(gameId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}