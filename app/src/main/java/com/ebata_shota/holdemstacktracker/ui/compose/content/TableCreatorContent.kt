package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.extension.labelResId
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorUiState

@Composable
fun TableCreatorContent(
    uiState: TableCreatorUiState.MainContent,
    onChangeSizeOfSB: (String) -> Unit,
    onChangeSizeOfBB: (String) -> Unit,
    onClickBetViewMode: (BetViewMode) -> Unit,
    onChangeStackSize: (String) -> Unit,
    onClickSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .selectableGroup()
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.game_type),
            modifier = Modifier.padding(top = 16.dp)
        )
        TableCreatorUiState.MainContent.GameType.entries.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (it == uiState.gameType),
                        onClick = { },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (it == uiState.gameType),
                    onClick = null
                )
                Text(
                    text = stringResource(it.labelResId),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.bet_view_type),
            modifier = Modifier.padding(top = 16.dp)
        )

        BetViewMode.entries.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (it == uiState.betViewMode),
                        onClick = { onClickBetViewMode(it) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (it == uiState.betViewMode),
                    onClick = null
                )
                Text(
                    text = stringResource(it.labelResId()),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        OutlinedTextFieldWithError(
            uiState = uiState.sbSize,
            onValueChange = onChangeSizeOfSB,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextFieldWithError(
            uiState = uiState.bbSize,
            onValueChange = onChangeSizeOfBB,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextFieldWithError(
            uiState = uiState.defaultStack,
            onValueChange = onChangeStackSize,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            // Submit Button
            Button(
                onClick = {
                    onClickSubmit()
                },
                enabled = uiState.enableSubmitButton,
            ) {
                Text(
                    text = stringResource(R.string.table_creator_submit)
                )
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun TableCreatorContentPreview() {
    HoldemStackTrackerTheme {
        TableCreatorContent(
            uiState = TableCreatorUiState.MainContent(),
            onChangeSizeOfSB = {},
            onChangeSizeOfBB = {},
            onClickBetViewMode = {},
            onChangeStackSize = {},
            onClickSubmit = {}
        )
    }
}