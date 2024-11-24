package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.parts.OutlinedTextFieldWithError
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme


@Composable
fun TableEditContent(
    uiState: TableEditContentUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextFieldWithError(
            uiState = uiState.hoge,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

data class TableEditContentUiState(
    val hoge: TextFieldErrorUiState
)

@Composable
@Preview(showBackground = true)
fun TableEditContentPreview() {
    HoldemStackTrackerTheme {
        TableEditContent(
            uiState = TableEditContentUiState(
                hoge = TextFieldErrorUiState(
                    label = R.string.sb_size_label,
                    value = TextFieldValue("b")
                )
            )
        )
    }
}