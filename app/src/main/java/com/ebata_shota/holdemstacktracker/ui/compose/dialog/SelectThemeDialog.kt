package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.ui.compose.row.RadioButtonRow
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEventWith
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectThemeDialog(
    uiState: SelectThemeDialogUiState,
    event: SelectThemeDialogEvent,
    modifier: Modifier = Modifier,
) {
    val delayState = rememberDelayState()
    BasicAlertDialog(
        onDismissRequest = { event.onDismissRequestSelectThemeDialog() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.change_theme_menu_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                uiState.themes.forEach {
                    RadioButtonRow(
                        item = it,
                        isSelected = it == uiState.selectedTheme,
                        labelString = {
                            stringResource(
                                when (it) {
                                    ThemeMode.LIGHT -> R.string.theme_mode_light
                                    ThemeMode.DARK -> R.string.theme_mode_dark
                                    ThemeMode.SYSTEM -> R.string.theme_mode_system
                                }
                            )
                        },
                        onClickBtnRadioButton = {
                            dropRedundantEventWith(delayState = delayState) {
                                event.onClickTheme(it)
                            }
                        }
                    )
                }
            }
        }
    }
}

data class SelectThemeDialogUiState(
    val selectedTheme: ThemeMode,
) {
    val themes = ThemeMode.entries.toTypedArray()
}

interface SelectThemeDialogEvent {
    fun onClickTheme(theme: ThemeMode)
    fun onDismissRequestSelectThemeDialog()
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun SelectBtnPlayerDialogPreview() {
    HoldemStackTrackerTheme {
        SelectThemeDialog(
            uiState = SelectThemeDialogUiState(
                selectedTheme = ThemeMode.SYSTEM
            ),
            event = object : SelectThemeDialogEvent {
                override fun onClickTheme(theme: ThemeMode) {}
                override fun onDismissRequestSelectThemeDialog() {}
            }
        )
    }
}

