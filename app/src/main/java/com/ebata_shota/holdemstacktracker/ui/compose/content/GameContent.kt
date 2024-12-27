package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.DelayState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropUselessDouble
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameContentUiState,
    onClickFoldButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onClickAllInButton: () -> Unit,
    onClickCallButton: () -> Unit,
    onClickRaiseButton: () -> Unit,
    onClickRaiseUpSizeButton: () -> Unit,
    onClickSliderTypeButton: () -> Unit,
    onChangeSlider: (Float) -> Unit,
    onClickSliderStepSwitch: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val delayState: DelayState = rememberDelayState()
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(4.dp)
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
                        .fillMaxHeight()
                        .weight(1.0f)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = AbsoluteAlignment.Left,
                    verticalArrangement = Arrangement.SpaceEvenly
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
                Column(
                    modifier = Modifier.weight(1.0f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CenterPanelContent(
                            uiState = uiState.centerPanelContentUiState
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
                                    uiState = playerUiState
                                )
                            }
                    }
                }
                // RIGHT
                Column(
                    modifier = modifier
                        .fillMaxHeight()
                        .weight(1.0f)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = AbsoluteAlignment.Right,
                    verticalArrangement = Arrangement.SpaceEvenly
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


            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            width = OutlineLabelBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = stringResource(R.string.label_blind),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Box(
                        modifier = Modifier
                            .height(height = 16.dp)
                            .width(OutlineLabelBorderWidth)
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = uiState.blindText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .heightIn(min = 56.dp),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickFoldButton()
                            }
                        },
                        enabled = uiState.isEnableFoldButton
                    ) {
                        Text(
                            text = stringResource(R.string.button_label_fold),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Button(
                        modifier = Modifier
                            .weight(2.0f)
                            .heightIn(min = 56.dp),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickCheckButton()
                            }
                        },
                        enabled = uiState.isEnableCheckButton
                    ) {
                        Text(
                            text = stringResource(R.string.button_label_check),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .heightIn(min = 56.dp),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickAllInButton()
                            }
                        },
                        enabled = uiState.isEnableAllInButton
                    ) {
                        Text(
                            text = stringResource(R.string.button_label_all_in),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickCallButton()
                            }
                        },
                        enabled = uiState.isEnableCallButton
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.button_label_call),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = uiState.callButtonSubText)
                        }
                    }
                    Button(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickRaiseButton()
                            }
                        },
                        enabled = uiState.isEnableRaiseButton
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.button_label_raise),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = uiState.raiseButtonSubText)
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(0.25f)
                            .padding(vertical = 4.dp),
                        onClick = {
                            dropUselessDouble(delayState) {
                                onClickRaiseUpSizeButton()
                            }
                        },
                        enabled = uiState.isEnableRaiseUpSizeButton,
                        shape = ButtonDefaults.shape
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.button_label_size),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = uiState.raiseUpSizeText
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onClickSliderTypeButton()
                        },
                        enabled = uiState.isEnableSliderTypeButton
                    ) {
                        val sliderTypeButtonLabelUiState = uiState.sliderTypeButtonLabelUiState
                        Text(
                            textAlign = TextAlign.Center,
                            text = when (sliderTypeButtonLabelUiState) {
                                is GameContentUiState.SliderTypeButtonLabelUiState.Stack -> {
                                    stringResource(sliderTypeButtonLabelUiState.labelResId)
                                }

                                is GameContentUiState.SliderTypeButtonLabelUiState.Pod -> {
                                    stringResource(
                                        sliderTypeButtonLabelUiState.labelResId,
                                        sliderTypeButtonLabelUiState.podSliderMaxRatio
                                    )
                                }
                            }
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "arrowDropDown"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                    ) {
                        val interactionSource = remember { MutableInteractionSource() }
                        Slider(
                            value = uiState.sliderPosition,
                            onValueChange = onChangeSlider,
                            // FIXME: マジックナンバーを解消
                            steps = if (uiState.isEnableSliderStep) {
                                when (uiState.sliderTypeButtonLabelUiState) {
                                    is GameContentUiState.SliderTypeButtonLabelUiState.Stack -> {
                                        9
                                    }

                                    is GameContentUiState.SliderTypeButtonLabelUiState.Pod -> {
                                        19
                                    }
                                }

                            } else {
                                0
                            },
                            valueRange = 0f..1f,
                            interactionSource = interactionSource,
                            enabled = uiState.isEnableSlider,
                            thumb = {
                                Label(
                                    label = {
                                        PlainTooltip(
                                            modifier = Modifier
                                                .sizeIn(45.dp, 25.dp)
                                                .wrapContentWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .widthIn(min = 50.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.label_stack),
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                                Text(
                                                    text = uiState.sliderLabelStackBody
                                                        .ifEmpty { stringResource(R.string.label_min) },
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    text = stringResource(R.string.label_pod),
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                                Text(
                                                    text = uiState.sliderLabelPodBody
                                                        .ifEmpty { stringResource(R.string.label_min) },
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    },
                                    interactionSource = interactionSource
                                ) {
                                    SliderDefaults.Thumb(
                                        interactionSource = interactionSource
                                    )
                                }
                            },
                            track = {
                                SliderDefaults.Track(
                                    sliderState = it
                                )
                            }
                        )
                    }
                    Switch(
                        modifier = Modifier
                            .padding(
                                start = 4.dp,
                                end = 4.dp
                            ),
                        checked = uiState.isEnableSliderStep,
                        onCheckedChange = {
                            onClickSliderStepSwitch(it)
                        },
                        enabled = uiState.isEnableSlider
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "2 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "2.5 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "3 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "4 BB")
                    }
                }
            }
        }
    }
}

data class GameContentUiState(
    val tableId: TableId,
    val game: Game,
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState,
    val blindText: String,
    // Fold
    val isEnableFoldButton: Boolean,
    // Check
    val isEnableCheckButton: Boolean,
    // AllIn
    val isEnableAllInButton: Boolean,
    // Call
    val isEnableCallButton: Boolean,
    val callButtonSubText: String,
    // Raise
    val isEnableRaiseButton: Boolean,
    val raiseButtonSubText: String,
    // RaiseUp
    val isEnableRaiseUpSizeButton: Boolean,
    val raiseUpSizeText: String,
    // SliderButton
    val isEnableSliderTypeButton: Boolean,
    val sliderTypeButtonLabelUiState: SliderTypeButtonLabelUiState,
    // Slider
    val isEnableSlider: Boolean,
    val sliderPosition: Float,
    val sliderLabelStackBody: String,
    val sliderLabelPodBody: String,
    // StepSwitch
    val isEnableSliderStep: Boolean,
) {
    sealed interface SliderTypeButtonLabelUiState {
        data object Stack : SliderTypeButtonLabelUiState {
            val labelResId = R.string.label_slider_type_stack
        }

        data class Pod(
            val podSliderMaxRatio: Int,
        ) : SliderTypeButtonLabelUiState {
            val labelResId = R.string.label_slider_type_pod
        }
    }
}

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
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = true,
                ),
                GamePlayerUiState(
                    playerName = "Player123456789",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    pendingBetSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false,
                    isBtn = false,
                )
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseTextResId = R.string.label_pre_flop,
                totalPod = "0"
            ),
            blindText = "100/200",
            isEnableFoldButton = true,
            isEnableCheckButton = true,
            isEnableAllInButton = true,
            isEnableCallButton = true,
            callButtonSubText = "+1",
            isEnableRaiseButton = true,
            raiseButtonSubText = "+100（=102)",
            isEnableSliderTypeButton = true,
            sliderTypeButtonLabelUiState = GameContentUiState.SliderTypeButtonLabelUiState.Stack,
            isEnableSlider = true,
            sliderPosition = 0.0f,
            isEnableSliderStep = true,
            sliderLabelStackBody = "",
            sliderLabelPodBody = "",
            isEnableRaiseUpSizeButton = true,
            raiseUpSizeText = "+10200",
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
            onClickFoldButton = {},
            onClickCheckButton = {},
            onClickAllInButton = {},
            onClickCallButton = {},
            onClickRaiseButton = {},
            onClickRaiseUpSizeButton = {},
            onClickSliderTypeButton = {},
            onChangeSlider = {},
            onClickSliderStepSwitch = {}
        )
    }
}