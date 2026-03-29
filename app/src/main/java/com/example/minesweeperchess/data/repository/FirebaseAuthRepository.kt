package com.example.minesweeperchess.data.repository

import com.example.minesweeperchess.domain.model.User
import com.example.minesweeperchess.domain.repository.AuthRepository
import com.example.minesweeperchess.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: User?
        get() = auth.currentUser?.let { firebaseUser ->
            User(id = firebaseUser.uid, email = firebaseUser.email.orEmpty())
        }

    override fun signIn(email: String, pass: String, onResult: (AuthResult) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(AuthResult.Success) }
            .addOnFailureListener { exception ->
                onResult(AuthResult.Error(exception.localizedMessage ?: "Ошибка входа"))
            }
    }

    override fun signInWithGoogle(idToken: String, onResult: (AuthResult) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { onResult(AuthResult.Success) }
            .addOnFailureListener { e -> onResult(AuthResult.Error(e.localizedMessage ?: "Ошибка Google")) }
    }

    override fun signUp(email: String, pass: String, onResult: (AuthResult) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(AuthResult.Success) }
            .addOnFailureListener { exception ->
                onResult(AuthResult.Error(exception.localizedMessage ?: "Ошибка регистрации"))
            }
    }

    override fun logout() {
        auth.signOut()
    }
}
