package com.example.minesweeperchess.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.minesweeperchess.ui.screen.*
import com.example.minesweeperchess.viewmodel.WaitingRoomViewModel

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
                navController.navigate(route) { popUpTo(Screen.Splash.route) { inclusive = true } }
            })
        }

        composable(Screen.Auth.route) {
            AuthScreen(onNavigateToMain = {
                navController.navigate(Screen.Lobby.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
            })
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
//                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToModeSelection = { navController.navigate(Screen.ModeSelection.route) },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) { popUpTo(Screen.Lobby.route) { inclusive = true } }
                },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onGameCreated = { gameId -> navController.navigate(Screen.WaitingRoom.createRoute(gameId)) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                canGoBack = true,
                onBack = { navController.popBackStack() },
                onProfileSaved = { navController.popBackStack() }
            )
        }

//        composable(Screen.Settings.route) {
//            SettingsScreen(onBack = { navController.popBackStack() })
//        }

        composable(Screen.ModeSelection.route) {
            MatchmakingScreen(
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

        composable(
            route = Screen.WaitingRoom.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            val viewModel: WaitingRoomViewModel = hiltViewModel()
            val isOpponentJoined by viewModel.isOpponentJoined.collectAsState()
            LaunchedEffect(gameId) { viewModel.startObserving(gameId) }
            WaitingRoomScreen(
                gameId = gameId,
                isOpponentJoined = isOpponentJoined,
                onBack = { navController.popBackStack() },
                onGameStarted = {
                    navController.navigate(Screen.Game.createRoute(gameId)) {
                        popUpTo(Screen.WaitingRoom.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            GameScreen(
                gameId = gameId,
                onQuit = {
                    navController.navigate(Screen.Lobby.route) { popUpTo(Screen.Lobby.route) { inclusive = true } }
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(onBack = { navController.popBackStack() })
        }
    }
}