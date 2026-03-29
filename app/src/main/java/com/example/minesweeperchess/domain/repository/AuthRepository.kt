package com.example.minesweeperchess.domain.repository

import com.example.minesweeperchess.domain.model.User

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    val currentUser: User?
    fun signIn(email: String, pass: String, onResult: (AuthResult) -> Unit)
    fun signInWithGoogle(idToken: String, onResult: (AuthResult) -> Unit)
    fun signUp(email: String, pass: String, onResult: (AuthResult) -> Unit)
    fun logout()
}