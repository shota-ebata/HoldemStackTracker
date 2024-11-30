package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainContent(
    uiState: MainContentUiState,
    navigateToTableCreator: () -> Unit,
    navigateToTableStandby: () -> Unit,
    navigateToJoinTableByQrActivity: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Button(
            onClick = {
                navigateToTableCreator()
            }
        ) {
            Text(
                text = "TableCreator"
            )
        }

        Button(
            onClick = {
                navigateToTableStandby()
            }
        ) {
            Text(
                text = "TableStandby"
            )
        }

        Button(
            onClick = {
                navigateToJoinTableByQrActivity()
            }
        ) {
            Text(
                text = "TableQr"
            )
        }
    }
}

data class MainContentUiState(
    val hoge: String
)