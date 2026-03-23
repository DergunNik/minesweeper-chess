package com.example.minesweeperchess.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.minesweeperchess.ui.component.MenuButton
import com.example.minesweeperchess.viewmodel.LobbyViewModel

@Composable
fun LobbyScreen(
    viewModel: LobbyViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onGameCreated: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.gameId) {
        uiState.gameId?.let { onGameCreated(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MINESWEEPER CHESS", style = MaterialTheme.typography.displaySmall)

        Spacer(modifier = Modifier.height(48.dp))

        MenuButton("Играть", Icons.Default.PlayArrow) {
            viewModel.toggleModeSelection(true)
        }

        MenuButton("Профиль", Icons.Default.Person) {
            onNavigateToProfile()
        }

        MenuButton("Настройки", Icons.Default.Settings) {
            onNavigateToSettings()
        }

        if (uiState.showModeSelection) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleModeSelection(false) },
                title = { Text("Выберите режим") },
                text = { Text("Пока доступен только PvP режим (через ID игры)") },
                confirmButton = {
                    Button(onClick = { viewModel.startPvPGame() }) {
                        Text("PvP Режим")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleModeSelection(false) }) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}
