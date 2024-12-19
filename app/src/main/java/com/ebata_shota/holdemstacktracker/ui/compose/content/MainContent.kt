package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropUselessDouble
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun MainContent(
    uiState: MainContentUiState,
    lazyListState: LazyListState,
    onClickTableRow: (TableId) -> Unit,
    modifier: Modifier = Modifier
) {
    val delayState = rememberDelayState()
    Box(
        modifier = modifier
    ) {
        if (uiState.tableSummaryList.isEmpty()) {
            // TODO: Tableがない場合
            Text("なしです")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState
        ) {
            items(
                items = uiState.tableSummaryList
            ) { item ->
                TableSummaryCardRow(
                    uiState = item,
                    onClickTableRow = { tableId ->
                        dropUselessDouble(delayState) {
                            onClickTableRow.invoke(tableId)
                        }
                    }
                )
            }
        }
    }
}

data class MainContentUiState(
    val tableSummaryList: List<TableSummaryCardRowUiState>
)


@Preview(showBackground = true, showSystemUi = true, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun MainContentPreview() {
    HoldemStackTrackerTheme {
        MainContent(
            uiState = MainContentUiState(
                tableSummaryList = emptyList()
            ),
            lazyListState = rememberLazyListState(),
            onClickTableRow = {}
        )
    }
}