package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState

@Composable
fun PlayersContent(
    uiState: GameMainPanelUiState,
    onClickCenterPanel: () -> Unit,
    onClickPlayerCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val leftPlayers = uiState.players.filter { it.playerPosition == GamePlayerUiState.PlayerPosition.LEFT }
    val rightPlayers = uiState.players.filter { it.playerPosition == GamePlayerUiState.PlayerPosition.RIGHT }

    val leftArrangement = if (leftPlayers.count() <= 1) Arrangement.Center else Arrangement.SpaceEvenly
    val rightArrangement = if (rightPlayers.count() <= 1) Arrangement.Center else Arrangement.SpaceEvenly

    Column(
        modifier = modifier
    ) {
        // TOP
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            uiState.players
                .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.TOP }
                .forEach { playerUiState ->
                    GamePlayerCard(
                        uiState = playerUiState,
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
                    .fillMaxHeight()
                    .padding(bottom = if (leftPlayers.count() == 3) 0.dp else 80.dp)
                    .weight(1.0f),
                horizontalAlignment = AbsoluteAlignment.Left,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Left,
                    verticalArrangement = leftArrangement,
                ) {
                    leftPlayers
                        .reversed()
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState,
                            )
                        }
                }
            }
            // CENTER
            Column(
                modifier = Modifier.weight(1.0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CenterPanelContent(
                        uiState = uiState.centerPanelContentUiState,
                        onClickCenterPanel = onClickCenterPanel
                    )
                }
                // BOTTOM
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.BOTTOM }
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState,
                            )
                        }
                }
            }
            // RIGHT
            Column(
                modifier = modifier
                    .fillMaxHeight()
                    .padding(bottom = if (rightPlayers.count() == 3) 0.dp else 80.dp)
                    .weight(1.0f),
                horizontalAlignment = AbsoluteAlignment.Right,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Right,
                    verticalArrangement = rightArrangement,
                ) {
                    rightPlayers
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState,
                            )
                        }
                }
            }
        }
    }
}

data class GameMainPanelUiState(
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState,
)

@Preview(
    showBackground = true,
    showSystemUi = false,
    name = "Light Mode",
    device = "spec:width=411dp,height=491dp"
)
@Composable
private fun PlayersContentPreview(
    @PreviewParameter(PlayersContentPreviewParameterProvider::class)
    uiState: GameMainPanelUiState,
) {
    PlayersContent(
        uiState = uiState,
        onClickCenterPanel = {},
        onClickPlayerCard = {}
    )
}
