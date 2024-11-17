package com.ebata_shota.holdemstacktracker.ui.compose.parts

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun OutlinedTextFieldWithError(
    uiState: TextFieldErrorUiState,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = uiState.value,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(uiState.label)) },
            isError = uiState.error != null,
            enabled = uiState.isEnabled,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.error != null) {
            Text(
                text = stringResource(uiState.error.errorMessageResId),
                color = Color.Red,
                modifier = Modifier
            )
        }
    }
}

private class PreviewParam : PreviewParameterProvider<ErrorMessage?> {
    override val values: Sequence<ErrorMessage?> = sequenceOf(
        null,
        ErrorMessage(R.string.input_error_message)
    )
}

@Composable
@Preview(showBackground = true)
fun OutlinedTextFieldWithErrorPreview(
    @PreviewParameter(PreviewParam::class) error: ErrorMessage?
) {
    HoldemStackTrackerTheme {
        OutlinedTextFieldWithError(
            uiState = TextFieldErrorUiState(
                label = R.string.sb_size_label,
                value = "100.0",
                error = error
            ),
            onValueChange = {}
        )
    }
}

data class TextFieldErrorUiState(
    @StringRes
    val label: Int,
    val value: String,
    val isEnabled: Boolean = true,
    val error: ErrorMessage? = null
)

data class ErrorMessage(
    @StringRes
    val errorMessageResId: Int
)