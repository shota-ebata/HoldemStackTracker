package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun CenterPanelContent(
    uiState: CenterPanelContentUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: フェーズ表示
            Text(
                text = stringResource(R.string.label_pod)
            )
            Row {
                Text(
                    text = uiState.totalPod
                )
            }
        }
    }
}

data class CenterPanelContentUiState(
    val totalPod: String
)

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun CenterPanelContentPreview() {
    HoldemStackTrackerTheme {
        CenterPanelContent(
            uiState = CenterPanelContentUiState(
                totalPod = "4"
            )
        )
    }
}