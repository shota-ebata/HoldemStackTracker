package com.ebata_shota.holdemstacktracker.ui.compose.row

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState.StackSize.EditableStackSize
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState.StackSize.NonEditableStackSize
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun UserEditRow(
    uiState: PlayerEditRowUiState,
    onChangeStackSize: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(56.dp)
            .padding(
                start = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = uiState.playerName,
            modifier = Modifier
                .weight(1.0f)
        )

        when (uiState.stackSize) {
            is EditableStackSize -> {
                OutlinedTextFieldWithError(
                    uiState = uiState.stackSize.stackSizeTextFieldUiState,
                    onValueChange = onChangeStackSize,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(start = 4.dp)
                        .padding(vertical = 4.dp)
                )
            }

            is NonEditableStackSize -> {
                Text(
                    text = uiState.stackSize.value,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 16.dp)
                        .padding(vertical = 4.dp),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
        ) {

            if (uiState.reorderable) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_reorder_24),
                        contentDescription = "",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

data class PlayerEditRowUiState(
    val playerId: PlayerId,
    val playerName: String,
    val stackSize: StackSize,
    val reorderable: Boolean
) {

    sealed interface StackSize {
        data class NonEditableStackSize(val value: String) : StackSize
        data class EditableStackSize(
            val stackSizeTextFieldUiState: TextFieldErrorUiState
        ) : StackSize
    }
}

private class PreviewParam : PreviewParameterProvider<PlayerEditRowUiState> {
    override val values: Sequence<PlayerEditRowUiState> = sequenceOf(
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = NonEditableStackSize("10000"),
            reorderable = false
        ),
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = EditableStackSize(
                TextFieldErrorUiState(
                    label = R.string.stack_size_label,
                    value = TextFieldValue("10000")
                )
            ),
            reorderable = true
        ),
    )
}

@Composable
@Preview(showBackground = true)
fun UserEditPreview(
    @PreviewParameter(PreviewParam::class)
    uiState: PlayerEditRowUiState
) {
    HoldemStackTrackerTheme {
        UserEditRow(
            uiState = uiState,
            onChangeStackSize = {}
        )
    }
}