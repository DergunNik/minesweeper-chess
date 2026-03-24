package com.example.minesweeperchess.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Profile : Screen("profile")
    object Lobby : Screen("lobby")
    object Game : Screen("game/{gameId}") {
        fun createRoute(gameId: String) = "game/$gameId"
    }
    object Settings : Screen("settings")
}