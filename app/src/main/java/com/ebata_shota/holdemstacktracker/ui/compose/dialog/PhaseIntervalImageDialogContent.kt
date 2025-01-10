package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhaseIntervalImageDialogContent(
    uiState: PhaseIntervalImageDialogUiState,
    onDismissDialogRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissDialogRequest() },
        modifier = modifier
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .clickable { onDismissDialogRequest() }
            ) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(uiState.imageResId),
                    contentDescription = "Phase Interval Image"
                )
            }
        }
    }
}

data class PhaseIntervalImageDialogUiState(
    @DrawableRes
    val imageResId: Int,
)

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PhaseIntervalImageDialogContentPreview() {
    HoldemStackTrackerTheme {
        PhaseIntervalImageDialogContent(
            uiState = PhaseIntervalImageDialogUiState(
                imageResId = R.drawable.flopimage
            ),
            onDismissDialogRequest = {},
        )
    }
}