package com.example.minesweeperchess.domain.repository

import com.example.minesweeperchess.domain.model.User

interface ProfileRepository {
    suspend fun saveProfile(profile: User): Result<Unit>
    suspend fun getProfile(uid: String): User?
    suspend fun uploadAvatar(uid: String, imageUri: android.net.Uri): Result<String>
}