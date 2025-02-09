package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameRuleDialog(
    uiState: TableCreatorContentUiState,
    event: EditGameRuleDialogEvent,
    modifier: Modifier = Modifier,
) {

    BasicAlertDialog(
        onDismissRequest = { event.onDismissEditGameRuleDialog() },
        modifier = modifier
    ) {
        Surface {
            TableCreatorContent(
                uiState = uiState,
                onChangeSizeOfSB = event::onChangeSizeOfSB,
                onChangeSizeOfBB = event::onChangeSizeOfBB,
                onChangeStackSize = event::onChangeDefaultStackSize,
                onClickSubmit = event::onClickEditGameRuleDialogSubmitButton
            )
        }
    }
}

interface EditGameRuleDialogEvent {
    fun onDismissEditGameRuleDialog()
    fun onChangeSizeOfSB(value: TextFieldValue)
    fun onChangeSizeOfBB(value: TextFieldValue)
    fun onChangeDefaultStackSize(value: TextFieldValue)
    fun onClickEditGameRuleDialogSubmitButton()
}