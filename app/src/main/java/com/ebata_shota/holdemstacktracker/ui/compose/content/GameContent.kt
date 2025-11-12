package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.toStringSource
import com.ebata_shota.holdemstacktracker.ui.compose.parts.RaiseSizeChangeButtonUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameContentUiState,
    onActionDisplayed: (ActionId?) -> Unit,
    onClickFoldButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onClickAllInButton: () -> Unit,
    onClickCallButton: () -> Unit,
    onClickRaiseButton: () -> Unit,
    onClickRaiseSizeButton: (Int) -> Unit,
    onClickMinusButton: () -> Unit,
    onClickPlusButton: () -> Unit,
    onClickSettingButton: () -> Unit,
    onClickPlayerCard: () -> Unit,
    onChangeSlider: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentActionId = uiState.currentActionId
    LaunchedEffect(currentActionId) {
        // ActionIDが変わるたびに、表示した扱い
        onActionDisplayed(currentActionId)
    }
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(4.dp)
        ) {
            PlayersContent(
                uiState = uiState,
                onClickPlayerCard = onClickPlayerCard,
                modifier = Modifier
                    .weight(weight = 1.0f),
            )

            // ここからコントロールパネル
            ControlPanelContent(
                uiState = uiState.controlPanelUiState,
                onClickFoldButton = onClickFoldButton,
                onClickCheckButton = onClickCheckButton,
                onClickAllInButton = onClickAllInButton,
                onClickCallButton = onClickCallButton,
                onClickRaiseButton = onClickRaiseButton,
                onClickRaiseSizeButton = onClickRaiseSizeButton,
                onClickMinusButton = onClickMinusButton,
                onChangeSlider = onChangeSlider,
                onClickPlusButton = onClickPlusButton,
                onClickSettingButton = onClickSettingButton,
            )

        }
    }
}

data class GameContentUiState(
    val tableIdString: StringSource,
    val currentActionId: ActionId?,
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState,
    val blindText: String,
    val controlPanelUiState: ControlPanelUiState,
)

private class GameContentUiStatePreviewParam :
    PreviewParameterProvider<GameContentUiState> {
    override val values: Sequence<GameContentUiState> = sequenceOf(
        GameContentUiState(
            tableIdString = StringSource(R.string.table_id_prefix, "123abc"),
            currentActionId = ActionId("actionId"),
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
                    lastActionText = StringSource(R.string.action_label_all_in),
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
                betPhaseText = StringSource(R.string.label_pre_flop),
                totalPot = StringSource("0"),
                pendingTotalBetSize = StringSource("2"),
                shouldShowBBSuffix = false
            ),
            blindText = "100/200",
            controlPanelUiState = ControlPanelUiState.ActiveControlPanelUiState(
                shouldShowBBSuffix = false,
                isEnableFoldButton = true,
                isEnableCheckButton = true,
                isEnableAllInButton = true,
                myPendingBetSizeStringSource = StringSource("0"),
                isEnableCallButton = true,
                callSizeStringSource = StringSource("200"),
                isEnableRaiseButton = true,
                raiseButtonMainLabelResId = R.string.button_label_raise,
                raiseSizeStringSource = StringSource("400"),
                raiseSizeButtonUiStates = listOf(
                    RaiseSizeChangeButtonUiState(
                        labelStringSource = StringSource("2 BB"),
                        raiseSize = 200,
                        isEnable = true,
                    ),
                    RaiseSizeChangeButtonUiState(
                        labelStringSource = StringSource("2.5 BB"),
                        raiseSize = 250,
                        isEnable = true,
                    ),
                    RaiseSizeChangeButtonUiState(
                        labelStringSource = StringSource("3 BB"),
                        raiseSize = 300,
                        isEnable = true,
                    ),
                    RaiseSizeChangeButtonUiState(
                        labelStringSource = StringSource("4 BB"),
                        raiseSize = 400,
                        isEnable = true,
                    ),
                ),
                isEnableMinusButton = true,
                isEnablePlusButton = true,
                isEnableSlider = true,
                sliderPosition = 0.0f,
                isEnableSliderStep = true,
                stackRatioText = "".toStringSource(),
                potRatioText = "".toStringSource(),
                isEnableRaiseUpSizeButton = true,
            )
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_6A,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_6A,
    name = "Dark Mode"
)
@Composable
private fun GameContentPreview(
    @PreviewParameter(GameContentUiStatePreviewParam::class)
    uiState: GameContentUiState
) {
    HoldemStackTrackerTheme {
        GameContent(
            uiState = uiState,
            onActionDisplayed = {},
            onClickFoldButton = {},
            onClickCheckButton = {},
            onClickAllInButton = {},
            onClickCallButton = {},
            onClickRaiseButton = {},
            onClickRaiseSizeButton = {},
            onClickMinusButton = {},
            onClickPlusButton = {},
            onClickSettingButton = {},
            onClickPlayerCard = {},
            onChangeSlider = {},
        )
    }
}