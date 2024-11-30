package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun MainContent(
    uiState: MainContentUiState,
    onClickFloatingButton: () -> Unit,
    onClickTableRow: (TableId) -> Unit,
    onClickQrScan: () -> Unit,
) {

    Scaffold(
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Button(
                    onClick = {
                        onClickTableRow(TableId("aa04a1a6-2da6-4462-9e8a-a679d8fa762f"))
                    }
                ) {
                    Text(
                        text = "TableRow"
                    )
                }
            }
        }
    }
}

data class MainContentUiState(
    val hoge: String
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
            uiState = MainContentUiState("hoge"),
            onClickFloatingButton = {},
            onClickTableRow = {},
            onClickQrScan = {}
        )
    }
}