package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme


@Composable
fun TableEditContent() {

}

data class TableEditContentUiState(
    val hoge: TextFieldErrorUiState
)

@Composable
@Preview(showBackground = true)
fun TableEditContentPreview() {
    HoldemStackTrackerTheme {
        TableEditContent()
    }
}