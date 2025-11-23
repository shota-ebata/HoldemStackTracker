package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState

@Composable
fun PlayersContent(
    uiState: GameMainPanelUiState,
    onClickCenterPanel: () -> Unit,
    onClickPlayerCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    .weight(1.0f),
                horizontalAlignment = AbsoluteAlignment.Left,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Left,
                    verticalArrangement = Arrangement.Top,
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.LEFT }
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
                    .weight(1.0f),
                horizontalAlignment = AbsoluteAlignment.Right,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Right,
                    verticalArrangement = Arrangement.Top,
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.RIGHT }
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

// TODO: 必要以上のデータを渡しているので修正
private class PlayersFor10ContentPreviewParameterProvider :
    PreviewParameterProvider<GameMainPanelUiState> {
    override val values: Sequence<GameMainPanelUiState> = sequenceOf(
        // 10人フル
        GameMainPanelUiState(
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = true,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = true,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true
                ),
                GamePlayerUiState(
                    playerName = "Player123456789",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = R.string.position_label_sb,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = R.string.position_label_bb,
                    lastActionText = StringSource(R.string.action_label_all_in),
                )
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                blindText = StringSource("100000/200000"),
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
        ),
        // 6人
        GameMainPanelUiState(
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = true,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = true,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true
                ),
                GamePlayerUiState(
                    playerName = "Player123456789",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = R.string.position_label_sb,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                blindText = StringSource("100/200"),
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
        ),
        // 4人
        GameMainPanelUiState(
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = true,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = true,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true
                ),
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                blindText = StringSource("100/200"),
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
        ),
        // 3人
        GameMainPanelUiState(
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = true,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = true,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_fold),
                    isFolded = true
                ),
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                blindText = StringSource("100/200"),
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
        ),
        // 2人
        GameMainPanelUiState(
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = true,
                    isCurrentPlayer = false,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_bet),
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = StringSource("198"),
                    shouldShowBBSuffix = false,
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = StringSource("2"),
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                    positionLabelResId = null,
                    lastActionText = StringSource(R.string.action_label_all_in),
                ),
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                blindText = StringSource("100/200"),
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
        ),
    )
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
    @PreviewParameter(PlayersFor10ContentPreviewParameterProvider::class)
    uiState: GameMainPanelUiState,
) {
    PlayersContent(
        uiState = uiState,
        onClickCenterPanel = {},
        onClickPlayerCard = {}
    )
}