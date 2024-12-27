package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth

@Composable
fun CenterPanelContent(
    uiState: CenterPanelContentUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val betPhaseTextResId = uiState.betPhaseTextResId
            if (betPhaseTextResId != null) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = stringResource(betPhaseTextResId),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Column(
                modifier = Modifier
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_pod),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = uiState.totalPod,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_bet),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = uiState.pendingTotalBetSize,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

data class CenterPanelContentUiState(
    val betPhaseTextResId: Int?,
    val totalPod: String,
    val pendingTotalBetSize: String,
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
                betPhaseTextResId = R.string.label_pre_flop,
                totalPod = "400",
                pendingTotalBetSize = "100"
            )
        )
    }
}