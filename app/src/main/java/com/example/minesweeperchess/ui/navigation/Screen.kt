package com.example.minesweeperchess.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Lobby : Screen("lobby")
    object ModeSelection : Screen("mode_selection")
    object Statistics : Screen("statistics")

    object WaitingRoom : Screen("waiting_room/{gameId}") {
        fun createRoute(gameId: String) = "waiting_room/$gameId"
    }

    object Game : Screen("game/{gameId}") {
        fun createRoute(gameId: String) = "game/$gameId"
    }
}