package com.example.minesweeperchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.minesweeperchess.ui.screen.*

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
                navController.navigate(Screen.Profile.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Profile.route) {
            val isFromLobby = navController.previousBackStackEntry?.destination?.route == Screen.Lobby.route
            ProfileScreen(
                canGoBack = isFromLobby,
                onBack = { navController.popBackStack() },
                onProfileSaved = {
                    if (isFromLobby) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.Lobby.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToModeSelection = { navController.navigate(Screen.ModeSelection.route) },
                onGameCreated = { gameId ->
                    navController.navigate(Screen.Game.createRoute(gameId))
                }
            )
        }

        composable(Screen.ModeSelection.route) {
            ModeSelectionScreen(
                onBack = { navController.popBackStack() },
                onGameCreated = { gameId ->
                    navController.navigate(Screen.WaitingRoom.createRoute(gameId)) {
                        popUpTo(Screen.ModeSelection.route) { inclusive = true }
                    }
                },
                onJoinedGame = { gameId ->
                    navController.navigate(Screen.Game.createRoute(gameId)) {
                        popUpTo(Screen.ModeSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WaitingRoom.route) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            WaitingRoomScreen(
                gameId = gameId,
                onBack = {
                    // TODO: viewModel.leaveGame()
                    navController.popBackStack()
                }
            )
        }

//        composable(Screen.Game.route) { backStackEntry ->
//            val gameId = backStackEntry.arguments?.getString("gameId") ?: "ID не найден"
//            GameScreen(
//                gameId = gameId,
//                onQuit = { navController.popBackStack(Screen.Lobby.route, false) }
//            )
//        }
    }
}