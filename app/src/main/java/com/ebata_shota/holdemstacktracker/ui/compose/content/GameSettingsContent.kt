package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun GameSettingsContent(
    uiState: GameSettingsContentUiState,
    onClickKeepScreenSwitch: (Boolean) -> Unit,
    onClickSettingSliderStepSwitch: (Boolean) -> Unit,
    onClickEnableAutoCheckFoldButtonSwitch: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rememberScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState)
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.backlight_high_24),
                    contentDescription = null
                )
            },
            headlineContent = {
                Text(
                    text = stringResource(R.string.setting_keep_screen_on),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                Switch(
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            end = 4.dp
                        ),
                    checked = uiState.isKeepScreenOn,
                    onCheckedChange = {
                        onClickKeepScreenSwitch(it)
                    }
                )
            }
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.setting_slider_step_on),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                Switch(
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            end = 4.dp
                        ),
                    checked = uiState.isEnableSliderStep,
                    onCheckedChange = {
                        onClickSettingSliderStepSwitch(it)
                    }
                )
            }
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.setting_auto_check_fold),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingContent = {
                Switch(
                    modifier = Modifier
                        .padding(
                            start = 24.dp,
                            end = 4.dp
                        ),
                    checked = uiState.isAutoCheckFoldButton,
                    onCheckedChange = {
                        onClickEnableAutoCheckFoldButtonSwitch(it)
                    }
                )
            }
        )
    }
}

data class GameSettingsContentUiState(
    val isKeepScreenOn: Boolean,
    val isEnableSliderStep: Boolean,
    val isAutoCheckFoldButton: Boolean,
)

interface GameSettingsContentEvent {
    fun onClickKeepScreenSwitch(isChecked: Boolean)
    fun onClickSettingSliderStepSwitch(isChecked: Boolean)
    fun onClickEnableAutoCheckFoldButtonSwitch(isChecked: Boolean)
}


@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun GameSettingsContentPreview() {
    HoldemStackTrackerTheme {
        GameSettingsContent(
            uiState = GameSettingsContentUiState(
                isKeepScreenOn = true,
                isEnableSliderStep = true,
                isAutoCheckFoldButton = false,
            ),
            onClickKeepScreenSwitch = {},
            onClickSettingSliderStepSwitch = {},
            onClickEnableAutoCheckFoldButtonSwitch = {},
        )
    }
}