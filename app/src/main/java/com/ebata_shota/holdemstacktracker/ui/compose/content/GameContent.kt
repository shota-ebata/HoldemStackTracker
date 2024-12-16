package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId

@Composable
fun GameContent(
    uiState: GameContentUiState,
    modifier: Modifier = Modifier
) {
    Text(text = "${uiState}")
}

data class GameContentUiState(
    val tableId: TableId,
    val game: Game,
    val isCurrentPlayer: Boolean
)