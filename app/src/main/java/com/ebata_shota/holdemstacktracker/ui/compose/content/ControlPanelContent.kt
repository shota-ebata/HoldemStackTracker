package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.toStringSource
import com.ebata_shota.holdemstacktracker.ui.compose.parts.RaiseSizeChangeButton
import com.ebata_shota.holdemstacktracker.ui.compose.parts.RaiseSizeChangeButtonUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.DelayState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.getChipString
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ControlPanelContent(
    uiState: ControlPanelUiState,
    onClickFoldButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onClickAllInButton: () -> Unit,
    onClickCallButton: () -> Unit,
    onClickRaiseButton: () -> Unit,
    onClickRaiseSizeButton: (Int) -> Unit,
    onClickMinusButton: () -> Unit,
    onChangeSlider: (Float) -> Unit,
    onClickPlusButton: () -> Unit,
    onClickSettingButton: () -> Unit,
    onClickSliderStepSwitch: (Boolean) -> Unit,
) {
    val delayState: DelayState = rememberDelayState()

    when (uiState) {
        is ControlPanelUiState.ActiveControlPanelUiState -> {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButtons(
                    uiState = uiState,
                    delayState = delayState,
                    onClickFoldButton = onClickFoldButton,
                    onClickCheckButton = onClickCheckButton,
                    onClickAllInButton = onClickAllInButton,
                    onClickCallButton = onClickCallButton,
                    onClickRaiseButton = onClickRaiseButton
                )
                RaiseSizeChangeButtons(
                    uiState = uiState,
                    onClickRaiseSizeButton = onClickRaiseSizeButton
                )
                BetSizeChangeSlider(
                    uiState = uiState,
                    onClickMinusButton = onClickMinusButton,
                    onChangeSlider = onChangeSlider,
                    onClickPlusButton = onClickPlusButton
                )
                BottomSetting(
                    uiState = uiState,
                    delayState = delayState,
                    onClickSettingButton = onClickSettingButton,
                    onClickSliderStepSwitch = onClickSliderStepSwitch
                )
            }
        }

        is ControlPanelUiState.InactiveControlPanelUiState -> {
            // Do nothing
        }

    }
}

@Composable
private fun ActionButtons(
    uiState: ControlPanelUiState.ActiveControlPanelUiState,
    delayState: DelayState,
    onClickFoldButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onClickAllInButton: () -> Unit,
    onClickCallButton: () -> Unit,
    onClickRaiseButton: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            modifier = Modifier
                .weight(1.0f)
                .heightIn(min = 56.dp),
            onClick = dropRedundantEvent(delayState = delayState) {
                onClickFoldButton()
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
            onClick = dropRedundantEvent(delayState = delayState) {
                onClickCheckButton()
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
            onClick = dropRedundantEvent(delayState = delayState) {
                onClickAllInButton()
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
        val myPendingBetSizeStringSource =
            uiState.myPendingBetSizeStringSource ?: StringSource("")
        Button(
            modifier = Modifier
                .weight(1.0f),
            onClick = dropRedundantEvent(delayState = delayState) {
                onClickCallButton()
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
                val callSizeStringSource =
                    uiState.callSizeStringSource ?: StringSource("")
                Text(
                    text = buildAnnotatedString {
                        if (uiState.isEnableCallButton) {
                            append(
                                getChipString(
                                    textStringSource = myPendingBetSizeStringSource,
                                    shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                                    suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
                                )
                            )
                            append(" ")
                            append(stringResource(R.string.left_arrow))
                            append(" ")
                            append(
                                getChipString(
                                    textStringSource = callSizeStringSource,
                                    shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                                    suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
                                )
                            )
                        } else {
                            append("")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Button(
            modifier = Modifier
                .weight(1.0f),
            onClick = dropRedundantEvent(delayState = delayState) {
                onClickRaiseButton()
            },
            enabled = uiState.isEnableRaiseButton
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(uiState.raiseButtonMainLabelResId),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = buildAnnotatedString {
                        if (uiState.isEnableRaiseButton) {
                            append(
                                getChipString(
                                    textStringSource = myPendingBetSizeStringSource,
                                    shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                                    suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
                                )
                            )
                            append(" ")
                            append(stringResource(R.string.left_arrow))
                            append(" ")
                            append(
                                getChipString(
                                    textStringSource = uiState.raiseSizeStringSource
                                        ?: StringSource(""),
                                    shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                                    suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
                                )
                            )
                        } else {
                            StringSource("")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun RaiseSizeChangeButtons(
    uiState: ControlPanelUiState.ActiveControlPanelUiState,
    onClickRaiseSizeButton: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        uiState.raiseSizeButtonUiStates.forEach {
            RaiseSizeChangeButton(
                modifier = Modifier
                    .weight(1.0f),
                uiState = it,
                onClickRaiseSizeButton = onClickRaiseSizeButton
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BetSizeChangeSlider(
    uiState: ControlPanelUiState.ActiveControlPanelUiState,
    onClickMinusButton: () -> Unit,
    onChangeSlider: (Float) -> Unit,
    onClickPlusButton: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClickMinusButton,
            enabled = uiState.isEnableMinusButton,
        ) {
            Icon(
                painter = painterResource(R.drawable.remove_24),
                contentDescription = null
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
                    19
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
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.widthIn(min = 50.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.label_stack),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = uiState.stackRatioText?.getString()
                                                .orEmpty(),
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.widthIn(min = 50.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.label_pot),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = uiState.potRatioText?.getString()
                                                .toString(),
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
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
        IconButton(
            onClick = onClickPlusButton,
            enabled = uiState.isEnablePlusButton,
        ) {
            Icon(
                painter = painterResource(R.drawable.add_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BottomSetting(
    uiState: ControlPanelUiState.ActiveControlPanelUiState,
    delayState: DelayState,
    onClickSettingButton: () -> Unit,
    onClickSliderStepSwitch: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = dropRedundantEvent(delayState = delayState) {
                    onClickSettingButton()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "setting"
                )
            }

            Switch(
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 4.dp
                    ),
                checked = uiState.isEnableSliderStep,
                onCheckedChange = {
                    onClickSliderStepSwitch(it)
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .widthIn(min = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.label_stack),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = uiState.stackRatioText?.getString().orEmpty(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Column(
                    modifier = Modifier
                        .widthIn(min = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.label_pot),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = uiState.potRatioText?.getString().orEmpty(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

sealed interface ControlPanelUiState {

    data class ActiveControlPanelUiState(
        val shouldShowBBSuffix: Boolean,
        // Fold
        val isEnableFoldButton: Boolean,
        // Check
        val isEnableCheckButton: Boolean,
        // AllIn
        val isEnableAllInButton: Boolean,
        val myPendingBetSizeStringSource: StringSource?,
        // Call
        val isEnableCallButton: Boolean,
        val callSizeStringSource: StringSource?,
        // Raise
        val isEnableRaiseButton: Boolean,
        @StringRes
        val raiseButtonMainLabelResId: Int,
        val raiseSizeStringSource: StringSource?,
        // RaiseUp
        val isEnableRaiseUpSizeButton: Boolean,
        // RaiseSizeButton
        val raiseSizeButtonUiStates: List<RaiseSizeChangeButtonUiState>,
        // MinusButton
        val isEnableMinusButton: Boolean,
        // Slider
        val isEnableSlider: Boolean,
        val sliderPosition: Float,
        val stackRatioText: StringSource?,
        val potRatioText: StringSource?,
        // PlusButton
        val isEnablePlusButton: Boolean,
        // StepSwitch
        val isEnableSliderStep: Boolean,
    ) : ControlPanelUiState

    data class InactiveControlPanelUiState(
        val isNextGame: Boolean,
    ) : ControlPanelUiState
}

class ControlPanelUiStatePreviewParam :
    PreviewParameterProvider<ControlPanelUiState> {
    override val values: Sequence<ControlPanelUiState> = sequenceOf(
        ControlPanelUiState.ActiveControlPanelUiState(
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
}


@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun ControlPanelContentPreview(
    @PreviewParameter(ControlPanelUiStatePreviewParam::class)
    uiState: ControlPanelUiState,
) {
    HoldemStackTrackerTheme {
        Surface {
            ControlPanelContent(
                uiState = uiState,
                onClickFoldButton = {},
                onClickCheckButton = {},
                onClickAllInButton = {},
                onClickCallButton = {},
                onClickRaiseButton = {},
                onClickRaiseSizeButton = {},
                onClickMinusButton = {},
                onChangeSlider = {},
                onClickPlusButton = {},
                onClickSettingButton = {},
                onClickSliderStepSwitch = {}
            )
        }
    }
}