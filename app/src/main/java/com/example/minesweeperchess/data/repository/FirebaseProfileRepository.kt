package com.example.minesweeperchess.data.repository

import android.net.Uri
import com.example.minesweeperchess.domain.model.User
import com.example.minesweeperchess.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProfileRepository {

    override suspend fun saveProfile(profile: User): Result<Unit> = try {
        firestore.collection("users").document(profile.id).set(profile).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getProfile(uid: String): User? = try {
        firestore.collection("users").document(uid).get().await()
            .toObject(User::class.java)
    } catch (e: Exception) {
        null
    }

    override suspend fun uploadAvatar(uid: String, imageUri: Uri): Result<String> = try {
        val storageRef = storage.reference.child("avatars/$uid.jpg")
        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        Result.success(downloadUrl)
    } catch (e: Exception) {
        Result.failure(e)
    }
}