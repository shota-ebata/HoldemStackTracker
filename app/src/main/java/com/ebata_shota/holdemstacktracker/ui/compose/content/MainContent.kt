package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardUiState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEventWith
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import java.time.LocalDateTime

@Composable
fun MainContent(
    uiState: MainContentUiState,
    onClickTableCreator: () -> Unit,
    onClickJoinTableByQr: () -> Unit,
    onClickJoinTableById: () -> Unit,
    onClickTableCard: (TableId) -> Unit,
    modifier: Modifier = Modifier
) {
    val delayState = rememberDelayState()
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            TableMainConsoleContent(
                onClickTableCreator = onClickTableCreator,
                onClickJoinTableByQr = onClickJoinTableByQr,
                onClickJoinTableById = onClickJoinTableById,
            )
            uiState.table?.let {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                )
                TableSummaryCard(
                    uiState = it,
                    onClickTableRow = { tableId ->
                        dropRedundantEventWith(delayState = delayState) {
                            onClickTableCard.invoke(tableId)
                        }
                    }
                )
            }
        }
    }
}

data class MainContentUiState(
    val table: TableSummaryCardUiState?,
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
                    table = TableSummaryCardUiState(
                        tableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e"),
                        gameTypeText = StringSource(R.string.game_type_ring),
                        blindText = StringSource("1/2"),
                        hostName = "ホスト名",
                        isJoined = false,
                        playerSize = "1/10",
                        updateTime = "2024/12/08 22:54:01",
                        createTime = LocalDateTime.now()
                    )
                ),
                onClickTableCreator = {},
                onClickJoinTableByQr = {},
                onClickJoinTableById = {},
                onClickTableCard = {}
            )
        }
    }
}