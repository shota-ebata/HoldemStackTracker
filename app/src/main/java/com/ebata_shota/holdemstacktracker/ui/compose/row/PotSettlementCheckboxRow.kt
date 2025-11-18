package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.extension.toDisableColor
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth
import com.ebata_shota.holdemstacktracker.ui.theme.SideSpace

@Composable
fun PotSettlementCheckboxRow(
    uiState: PotSettlementCheckboxRowUiState,
    onClickRow: (PotSettlementCheckboxRowUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .let {
                if (uiState.isEnable) {
                    it.selectable(
                        selected = uiState.isSelected,
                        onClick = { onClickRow(uiState) },
                        role = Role.Checkbox
                    )
                } else {
                    it
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.isSelected,
                enabled = uiState.isEnable,
                onCheckedChange = null,
                modifier = Modifier
                    .padding(start = SideSpace)
            )
            Text(
                text = uiState.playerName.getString(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.isEnable) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.toDisableColor()
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(end = 16.dp)
            )

        }
        if (uiState.shouldShowFoldLabel) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = SideSpace)
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 8.dp),

                text = stringResource(R.string.action_label_fold),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class PotSettlementCheckboxRowUiState(
    val playerId: PlayerId,
    val playerName: StringSource,
    val isSelected: Boolean = false,
    val isEnable: Boolean = true,
    val shouldShowFoldLabel: Boolean = false,
)

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun PotSettlementCheckboxRowPreview() {
    HoldemStackTrackerTheme {
        Surface {
            PotSettlementCheckboxRow(
                uiState = PotSettlementCheckboxRowUiState(
                    playerId = PlayerId("hoge"),
                    playerName = StringSource("123456789012345678901234567890"),
                    shouldShowFoldLabel = true
                ),
                onClickRow = {}
            )
        }
    }
}