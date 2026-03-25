package com.example.minesweeperchess.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalClipboardManager
import kotlinx.coroutines.launch

@Composable
fun WaitingRoomScreen(
    gameId: String,
    isOpponentJoined: Boolean,
    onBack: () -> Unit,
    onGameStarted: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isOpponentJoined) {
        if (isOpponentJoined) {
            onGameStarted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ожидание соперника",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.clickable {
                clipboardManager.setText(AnnotatedString(gameId))
                scope.launch {
                    snackbarHostState.showSnackbar("Код скопирован в буфер обмена")
                }
            }
        ) {
            Text(
                text = gameId,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Отмена")
        }
    }
}