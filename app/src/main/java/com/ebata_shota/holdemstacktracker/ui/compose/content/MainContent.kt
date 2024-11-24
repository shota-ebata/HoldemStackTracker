package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable

@Composable
fun MainContent(
    uiState: MainContentUiState
) {
}

data class MainContentUiState(
    val textFieldState: TextFieldState
)