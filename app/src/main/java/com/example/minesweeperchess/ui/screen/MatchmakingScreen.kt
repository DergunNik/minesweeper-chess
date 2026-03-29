package com.example.minesweeperchess.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.minesweeperchess.ui.component.MenuButton
import com.example.minesweeperchess.viewmodel.LobbyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(
    onBack: () -> Unit,
    onGameCreated: (String) -> Unit,
    onJoinedGame: (String) -> Unit,
    viewModel: LobbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var inputGameId by remember { mutableStateOf("") }

    LaunchedEffect(uiState.gameId, uiState.isJoined) {
        if (uiState.isJoined && uiState.gameId != null) {
            onJoinedGame(uiState.gameId!!)
        } else if (uiState.gameId != null) {
            onGameCreated(uiState.gameId!!)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PvP Шахматы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Создать новую игру", style = MaterialTheme.typography.titleMedium)
            MenuButton(
                text = "Создать лобби",
                icon = Icons.Default.PlayArrow,
                onClick = { viewModel.startPvPGame() }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Присоединиться по коду", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = inputGameId,
                onValueChange = { if (it.length <= 6) inputGameId = it },
                label = { Text("Код игры") },
                placeholder = { Text("Напр. 123456") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Button(
                onClick = { viewModel.joinGame(inputGameId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputGameId.length == 6 && !uiState.isLoading
            ) {
                Text("Подключиться")
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}