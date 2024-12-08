package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.theme.CardSideSpace
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import java.time.LocalDateTime

@Composable
fun TableSummaryCardRow(
    uiState: TableSummaryCardRowUiState,
    onClickTableRow: (TableId) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CardSideSpace),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.isJoined) {
                MaterialTheme.colorScheme.inversePrimary
            } else {
                Color.Unspecified
            }
        ),
        onClick = {
            onClickTableRow(uiState.tableId)
        },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (uiState.isJoined) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(36.dp),
                        painter = painterResource(R.drawable.baseline_person_pin_24),
                        contentDescription = "person_pin"
                    )
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.label_joined)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(R.string.label_blind)
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = uiState.blindText
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(R.string.label_host)
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = uiState.hostName
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(R.string.label_update_datetime)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = uiState.updateTime
                )
            }
        }
    }
}

data class TableSummaryCardRowUiState(
    val tableId: TableId,
    val blindText: String,
    val hostName: String,
    val isJoined: Boolean,
    val updateTime: String,
    val createTime: LocalDateTime
)

private class TableSummaryCardRowPreviewParam :
    PreviewParameterProvider<TableSummaryCardRowUiState> {
    override val values: Sequence<TableSummaryCardRowUiState> = sequenceOf(
        TableSummaryCardRowUiState(
            tableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e"),
            blindText = "1/2",
            hostName = "ホスト名",
            isJoined = false,
            updateTime = "2024/12/08 22:54:01",
            createTime = LocalDateTime.now()
        ),
        TableSummaryCardRowUiState(
            tableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e"),

            hostName = "ホスト名",
            blindText = "1/2",
            isJoined = true,
            updateTime = "2024/12/08 22:54:01",
            createTime = LocalDateTime.now()
        ),
    )
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun TableSummaryCardRowPreview(
    @PreviewParameter(TableSummaryCardRowPreviewParam::class)
    uiState: TableSummaryCardRowUiState
) {
    HoldemStackTrackerTheme {
        TableSummaryCardRow(
            uiState = uiState,
            onClickTableRow = {}
        )
    }
}