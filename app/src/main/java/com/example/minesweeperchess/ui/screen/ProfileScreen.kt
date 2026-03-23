package com.example.minesweeperchess.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.minesweeperchess.ui.component.ImagePicker
import com.example.minesweeperchess.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onProfileSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onProfileSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Настройка профиля",
            style = MaterialTheme.typography.headlineMedium
        )

        AsyncImage(
            model = uiState.selectedUri ?: uiState.avatarUrl.ifEmpty { viewModel.defaultAvatars[0] },
            contentDescription = "Avatar preview",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Text("Выберите аватар:")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.defaultAvatars.forEachIndexed { index, url ->
                OutlinedButton(
                    onClick = { viewModel.selectDefaultAvatar(url) },
                    border = if (uiState.avatarUrl == url)
                        ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                    else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Вариант ${index + 1}")
                }
            }
        }

        ImagePicker(onImageSelected = { viewModel.selectCustomImage(it) })

        OutlinedTextField(
            value = uiState.nickname,
            onValueChange = { viewModel.onNicknameChange(it) },
            label = { Text("Ваш никнейм") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.nickname.isNotBlank()
            ) {
                Text("Сохранить и продолжить")
            }
        }
    }
}