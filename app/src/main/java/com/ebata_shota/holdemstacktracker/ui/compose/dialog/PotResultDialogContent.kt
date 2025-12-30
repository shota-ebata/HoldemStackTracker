package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.row.PotResultRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.PotResultRowUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotResultDialogContent(
    uiState: PotResultDialogUiState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Content(uiState)
    }
}

@Composable
private fun Content(
    uiState: PotResultDialogUiState,
) {
    Surface {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            uiState.potResults.forEach { rowUiState ->
                PotResultRow(
                    uiState = rowUiState
                )
            }
        }
    }
}

data class PotResultDialogUiState(
    val potResults: List<PotResultRowUiState>,
)


@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PotResultDialogContentPreview() {
    HoldemStackTrackerTheme {
        Surface {
            Content(
                PotResultDialogUiState(
                    potResults = listOf(
                        PotResultRowUiState(
                            potLabelText = StringSource("Side Pot 1"),
                            potSizeText = StringSource("1000"),
                            winnerPlayerNames = listOf(
                                StringSource("櫻木"),
                                StringSource("風野"),
                            ),
                        ),
                        PotResultRowUiState(
                            potLabelText = StringSource("Main Pot"),
                            potSizeText = StringSource("1000"),
                            winnerPlayerNames = listOf(
                                StringSource("櫻木"),
                            ),
                        ),
                    )
                )
            )
        }
    }
}