package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableSummary
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.TableSummaryCardRowUiState
import com.ebata_shota.holdemstacktracker.ui.theme.CardSideSpace
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import java.time.LocalDateTime

@Composable
fun MainContent(
    uiState: MainContentUiState,
    onClickFloatingButton: () -> Unit,
    onClickTableRow: (TableId) -> Unit,
    onClickQrScan: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        onClickQrScan()
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_qr_code_scanner_24),
                        contentDescription = "QR Scanner"
                    )
                }
                FloatingActionButton(
                    onClick = {
                        onClickFloatingButton()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->

        if (uiState.tableSummaryList.isEmpty()) {
            Text("なしです")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = uiState.tableSummaryList
            ) { item ->
                TableSummaryCardRow(item, onClickTableRow)
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
                tableSummaryList = listOf(
                    TableSummaryCardRowUiState(
                        tableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e"),
                        blindText = "1/2",
                        hostName = "ホスト名",
                        isJoined = true,
                        updateTime = "更新日時",
                        createTime = LocalDateTime.now()
                    )
                )
            ),
            onClickFloatingButton = {},
            onClickTableRow = {},
            onClickQrScan = {}
        )
    }
}