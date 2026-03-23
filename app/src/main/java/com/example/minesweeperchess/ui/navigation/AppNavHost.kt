package com.example.minesweeperchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.minesweeperchess.ui.screen.AuthScreen
import com.example.minesweeperchess.ui.screen.LobbyScreen
import com.example.minesweeperchess.ui.screen.ProfileScreen
import com.example.minesweeperchess.ui.screen.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Auth.route) {
            AuthScreen(onNavigateToMain = {
                navController.navigate(Screen.Splash.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onProfileSaved = {
                navController.navigate(Screen.Lobby.route) {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Lobby.route) {
            androidx.compose.material3.Text("Добро пожаловать в Лобби!")
        }

        composable(Screen.Game.route) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: "ID не найден"
            androidx.compose.material3.Text("Игра номер: $gameId")
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { /* TODO: добавить настройки */ },
                onGameCreated = { gameId ->
                    navController.navigate(Screen.Game.createRoute(gameId))
                }
            )
        }
    }
}