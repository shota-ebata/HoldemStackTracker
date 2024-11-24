package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun MainContent(
    uiState: MainContentUiState,
    onChangeText: (TextFieldValue) -> Unit
) {
    // TODO: uiStateやonChangeTextを用いてViewを定義する
}

data class MainContentUiState(
    val textFieldValue: TextFieldValue
)


private class PreviewParam : PreviewParameterProvider<MainContentUiState> {
    override val values: Sequence<MainContentUiState> = sequenceOf(
        MainContentUiState(
            textFieldValue = TextFieldValue("hoge")
        ),
        MainContentUiState(
            textFieldValue = TextFieldValue("fuga")
        )
    )
}

@Composable
@Preview(showBackground = true)
fun MainContentPreview(
    @PreviewParameter(PreviewParam::class) uiState: MainContentUiState
) {
    HoldemStackTrackerTheme {
        MainContent(
            uiState = uiState,
            onChangeText = {}
        )
    }
}