package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import java.time.Instant

@Composable
fun GameContent(
    uiState: GameContentUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            // TOP
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                uiState.players
                    .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.TOP }
                    .forEach { playerUiState ->
                        GamePlayerCard(
                            uiState = playerUiState
                        )
                    }
            }
            Row(
                modifier = Modifier
                    .weight(weight = 1.0f)
            ) {
                // LEFT
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Left,
                    verticalArrangement = Arrangement.Center
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.LEFT }
                        .reversed()
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState
                            )
                        }
                }
                // CENTER
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    CenterPanelContent(
                        uiState = uiState.centerPanelContentUiState
                    )
                }
                // RIGHT
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Right,
                    verticalArrangement = Arrangement.Center
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.RIGHT }
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState
                            )
                        }
                }
            }

            // BOTTOM
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                uiState.players
                    .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.BOTTOM }
                    .forEach { playerUiState ->
                        GamePlayerCard(
                            uiState = playerUiState
                        )
                    }
            }

            Row(
                modifier = Modifier
                    .weight(weight = 0.5f)
            ) {
                Text(text = "$uiState")
            }
        }
    }
}

data class GameContentUiState(
    val tableId: TableId,
    val game: Game,
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState
)

private class GameContentUiStatePreviewParam :
    PreviewParameterProvider<GameContentUiState> {
    override val values: Sequence<GameContentUiState> = sequenceOf(
        GameContentUiState(
            tableId = TableId("tableId"),
            game = Game(
                version = 0,
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                players = emptySet(),
                podList = emptyList(),
                phaseList = emptyList(),
                updateTime = Instant.now()
            ),
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true
                )
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                totalPod = "0"
            )
        )
    )
}

@Preview(
    widthDp = 480,
    showBackground = true,
    showSystemUi = true,
    name = "Light Mode"
)
@Preview(
    widthDp = 480,
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun GameContentPreview(
    @PreviewParameter(GameContentUiStatePreviewParam::class)
    uiState: GameContentUiState
) {
    HoldemStackTrackerTheme {
        GameContent(
            uiState = uiState
        )
    }
}