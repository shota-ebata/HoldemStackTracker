package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.UserEditRow
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme


@Composable
fun TableEditContent(
    uiState: TableEditContentUiState,
    onChangeStackSize: (PlayerId, TextFieldValue) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(56.dp)
                .padding(
                    start = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Player名",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1.0f)
            )

            Text(
                text = stringResource(R.string.stack_size_label),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1.0f)
                    .padding(end = 16.dp)
                    .padding(vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(uiState.playerEditRows) { index, playerEditRowUiState ->
                UserEditRow(
                    uiState = playerEditRowUiState,
                    onChangeStackSize = {
                        onChangeStackSize.invoke(playerEditRowUiState.playerId, it)
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
                if (index < uiState.playerEditRows.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

data class TableEditContentUiState(
    val playerEditRows: List<PlayerEditRowUiState>,
    val isAddable: Boolean
)


private class PreviewParam : PreviewParameterProvider<TableEditContentUiState> {
    override val values: Sequence<TableEditContentUiState> = sequenceOf(
        TableEditContentUiState(
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = PlayerEditRowUiState.StackSize.NonEditableStackSize("10000"),
                    reorderable = false
                )
            },
            isAddable = false
        ),
        TableEditContentUiState(
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = PlayerEditRowUiState.StackSize.EditableStackSize(
                        stackSizeTextFieldUiState = TextFieldErrorUiState(
                            value = TextFieldValue("10000")
                        )
                    ),
                    reorderable = true
                )
            },
            isAddable = true
        )
    )
}

@Composable
@Preview(showBackground = true)
fun TableEditContentPreview(
    @PreviewParameter(PreviewParam::class) uiState: TableEditContentUiState
) {
    HoldemStackTrackerTheme {
        TableEditContent(
            uiState = uiState,
            onChangeStackSize = { _, _ -> }
        )
    }
}