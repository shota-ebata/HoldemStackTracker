package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ebata_shota.holdemstacktracker.domain.model.Game

@Composable
fun GameContent(
    uiState: GameContentUiState,
    modifier: Modifier = Modifier
) {
    Text(text = "${uiState.game}")
}

data class GameContentUiState(
    val game: Game
)