package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.UserEditRow
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme


@Composable
fun TableEditContent(
    uiState: TableEditContentUiState,
    getTableQrBitmap: suspend (TableId) -> Painter,
    onClickStackEditButton: (PlayerId, String) -> Unit,
    onClickUpButton: (PlayerId) -> Unit,
    onClickDownButton: (PlayerId) -> Unit,
) {
    var qrPainter: Painter? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        qrPainter = getTableQrBitmap.invoke(uiState.tableId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        qrPainter?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = it,
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(56.dp)
                .padding(
                    start = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Player名",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1.0f)
            )

            Text(
                text = stringResource(R.string.stack_size_label),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1.0f)
                    .padding(end = 16.dp)
                    .padding(vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(uiState.playerEditRows) { index, playerEditRowUiState ->
                UserEditRow(
                    uiState = playerEditRowUiState,
                    onClickStackEditButton = {
                        onClickStackEditButton(
                            playerEditRowUiState.playerId,
                            playerEditRowUiState.stackSize
                        )
                    },
                    onClickUpButton = {
                        onClickUpButton(playerEditRowUiState.playerId)
                    },
                    onClickDownButton = {
                        onClickDownButton(playerEditRowUiState.playerId)
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
                if (index < uiState.playerEditRows.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

data class TableEditContentUiState(
    val tableId: TableId,
    val playerEditRows: List<PlayerEditRowUiState>,
    val isAddable: Boolean
)


private class PreviewParam : PreviewParameterProvider<TableEditContentUiState> {
    override val values: Sequence<TableEditContentUiState> = sequenceOf(
        TableEditContentUiState(
            tableId = TableId("tableId"),
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isEditable = false
                )
            },
            isAddable = false
        ),
        TableEditContentUiState(
            tableId = TableId("tableId"),
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isEditable = true
                )
            },
            isAddable = true
        )
    )
}

@Composable
@Preview(showBackground = true)
fun TableEditContentPreview(
    @PreviewParameter(PreviewParam::class) uiState: TableEditContentUiState
) {
    val painter = painterResource(R.drawable.baseline_qr_code_2_24)
    HoldemStackTrackerTheme {
        TableEditContent(
            uiState = uiState,
            getTableQrBitmap = { painter },
            onClickStackEditButton = { _, _ -> },
            onClickUpButton = {},
            onClickDownButton = {}
        )
    }
}