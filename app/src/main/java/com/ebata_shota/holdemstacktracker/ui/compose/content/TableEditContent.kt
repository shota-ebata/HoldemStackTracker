package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ebata_shota.holdemstacktracker.ui.compose.row.RadioButtonRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.UserEditRow
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.SideSpace


@Composable
fun TableEditContent(
    uiState: TableEditContentUiState,
    getTableQrPainter: () -> Painter?,
    onClickStackEditButton: (PlayerId, String) -> Unit,
    onClickUpButton: (PlayerId) -> Unit,
    onClickDownButton: (PlayerId) -> Unit,
    onChangeBtnChosen: (TableEditContentUiState.BtnChosen) -> Unit,
    onClickSubmitButton: () -> Unit
) {
    val qrPainter = getTableQrPainter()
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            // スクロール
            val rememberScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState)
                    .weight(1.0f)
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
                Text(
                    text = stringResource(R.string.member_title_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = SideSpace)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(56.dp)
                        .padding(horizontal = SideSpace),
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    uiState.playerEditRows.forEachIndexed { index, playerEditRowUiState ->
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
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = SideSpace)
                            )
                        }
                    }
                }
                // BTNの決め方
                if (uiState.isEditable) {
                    Text(
                        text = stringResource(R.string.btn_chosen),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .padding(horizontal = SideSpace)
                    )

                    TableEditContentUiState.BtnChosen.entries.forEach { item ->
                        RadioButtonRow(
                            item = item,
                            isSelected = item == uiState.btnChosen,
                            label = { it.labelStrId },
                            onClickBtnRadioButton = onChangeBtnChosen
                        )
                    }
                }
            }

            // ゲーム開始ボタン
            if (uiState.isEditable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onClickSubmitButton,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState.enableSubmitButton
                    ) {
                        Text(stringResource(R.string.start_game))
                    }
                }
            }
        }
    }
}

data class TableEditContentUiState(
    val tableId: TableId,
    val playerEditRows: List<PlayerEditRowUiState>,
    val btnChosen: BtnChosen,
    val enableSubmitButton: Boolean,
    val isEditable: Boolean
) {
    enum class BtnChosen {
        RANDOM,
        SELECT;

        @get:StringRes
        val labelStrId: Int
            get() = when (this@BtnChosen) {
                RANDOM -> R.string.btn_random
                SELECT -> R.string.btn_select
            }
    }
}


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
            btnChosen = TableEditContentUiState.BtnChosen.RANDOM,
            enableSubmitButton = true,
            isEditable = false
        ),
        TableEditContentUiState(
            tableId = TableId("tableId"),
            playerEditRows = (0..1).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isEditable = true
                )
            },
            btnChosen = TableEditContentUiState.BtnChosen.SELECT,
            enableSubmitButton = true,
            isEditable = true
        )
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun TableEditContentPreview(
    @PreviewParameter(PreviewParam::class) uiState: TableEditContentUiState
) {
    val painter = painterResource(R.drawable.baseline_qr_code_2_24)
    HoldemStackTrackerTheme {
        TableEditContent(
            uiState = uiState,
            getTableQrPainter = { painter },
            onClickStackEditButton = { _, _ -> },
            onClickUpButton = {},
            onClickDownButton = {},
            onChangeBtnChosen = {},
            onClickSubmitButton = {}
        )
    }
}