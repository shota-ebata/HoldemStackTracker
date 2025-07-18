package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.GameType
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.extension.labelResId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ErrorMessage
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun TableCreatorContent(
    uiState: TableCreatorContentUiState,
    onChangeSizeOfSB: (TextFieldValue) -> Unit,
    onChangeSizeOfBB: (TextFieldValue) -> Unit,
    onChangeStackSize: (TextFieldValue) -> Unit,
    onClickSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .selectableGroup()
                .padding(16.dp)
        ) {
            // FIXME: GameTypeが実装されたときに
//            Text(
//                text = stringResource(R.string.game_type),
//                modifier = Modifier.padding(top = 16.dp)
//            )
//            GameType.entries.forEach {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .selectable(
//                            selected = (it == uiState.gameType),
//                            onClick = { },
//                            role = Role.RadioButton
//                        )
//                        .padding(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    RadioButton(
//                        selected = (it == uiState.gameType),
//                        onClick = null
//                    )
//                    Text(
//                        text = stringResource(it.labelResId()),
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = 16.dp)
//                    )
//                }
//            }

            OutlinedTextFieldWithError(
                uiState = uiState.sbSize,
                onValueChange = onChangeSizeOfSB,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextFieldWithError(
                uiState = uiState.bbSize,
                onValueChange = onChangeSizeOfBB,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextFieldWithError(
                uiState = uiState.defaultStack,
                onValueChange = onChangeStackSize,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (uiState.bottomErrorMessage != null) {
                Text(
                    text = uiState.bottomErrorMessage.errorMessage.getString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Submit Button
                Button(
                    onClick = dropRedundantEvent {
                        onClickSubmit()
                    },
                    enabled = uiState.enableSubmitButton,
                ) {
                    Text(
                        text = uiState.submitButtonLabel.getString()
                    )
                }
            }
        }
    }

}

data class TableCreatorContentUiState(
    val gameType: GameType,
    val sbSize: TextFieldErrorUiState,
    val bbSize: TextFieldErrorUiState,
    val defaultStack: TextFieldErrorUiState,
    val submitButtonLabel: StringSource,
    val bottomErrorMessage: ErrorMessage?
) {
    val enableSubmitButton: Boolean
        get() = sbSize.error == null && bbSize.error == null && defaultStack.error == null
}


@Preview(showBackground = true, showSystemUi = true, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun TableCreatorContentPreview() {
    HoldemStackTrackerTheme {
        TableCreatorContent(
            uiState = TableCreatorContentUiState(
                gameType = GameType.RingGame,
                sbSize = TextFieldErrorUiState(
                    label = R.string.sb_size_label,
                    value = TextFieldValue("0")
                ),
                bbSize = TextFieldErrorUiState(
                    label = R.string.bb_size_label,
                    value = TextFieldValue("0")
                ),
                defaultStack = TextFieldErrorUiState(
                    label = R.string.default_stack_label,
                    value = TextFieldValue("0")
                ),
                submitButtonLabel = StringSource(R.string.table_creator_submit),
                bottomErrorMessage = null
            ),
            onChangeSizeOfSB = {},
            onChangeSizeOfBB = {},
            onChangeStackSize = {},
            onClickSubmit = {}
        )
    }
}