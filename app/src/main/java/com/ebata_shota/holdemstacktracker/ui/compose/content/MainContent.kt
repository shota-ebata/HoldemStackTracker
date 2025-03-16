package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEventWith
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun MainContent(
    uiState: MainContentUiState,
    lazyListState: LazyListState,
    onClickTableCreator: () -> Unit,
    onClickJoinTableByQr: () -> Unit,
    onClickJoinTableById: () -> Unit,
    onClickTableRow: (TableId) -> Unit,
    modifier: Modifier = Modifier
) {
    val delayState = rememberDelayState()
    Box(
        modifier = modifier
    ) {
        if (uiState.tableSummaryList.isEmpty()) {
            // 参加テーブルが存在しない場合
            TableMainConsoleContent(
                onClickTableCreator = onClickTableCreator,
                onClickJoinTableByQr = onClickJoinTableByQr,
                onClickJoinTableById = onClickJoinTableById,
            )
        } else {
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
                            dropRedundantEventWith(delayState = delayState) {
                                onClickTableRow.invoke(tableId)
                            }
                        }
                    )
                }
            }
        }
    }
}

data class MainContentUiState(
    val tableSummaryList: List<TableSummaryCardRowUiState>
)

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun MainContentPreview() {
    HoldemStackTrackerTheme {
        Surface {
            MainContent(
                uiState = MainContentUiState(
                    tableSummaryList = emptyList()
                ),
                lazyListState = rememberLazyListState(),
                onClickTableCreator = {},
                onClickJoinTableByQr = {},
                onClickJoinTableById = {},
                onClickTableRow = {}
            )
        }
    }
}