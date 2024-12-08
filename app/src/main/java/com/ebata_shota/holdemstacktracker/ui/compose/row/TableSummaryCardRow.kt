package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        onClick = {
            onClickTableRow(uiState.tableId)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = uiState.tableId.value
            )
            Text(
                text = LocalDateTime.now().toString()
            )
        }
    }
}

data class TableSummaryCardRowUiState(
    val tableId: TableId,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
)

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun TableSummaryCardRowPreview() {
    HoldemStackTrackerTheme {
        TableSummaryCardRow(
            uiState =
            TableSummaryCardRowUiState(
                tableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e"),
                updateTime = LocalDateTime.now(),
                createTime = LocalDateTime.now()
            ),
            onClickTableRow = {}
        )
    }
}