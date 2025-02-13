package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContentUiState.BtnChosenUiState
import com.ebata_shota.holdemstacktracker.ui.compose.extension.labelResId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.BlindTextLabel
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.RadioButtonRow
import com.ebata_shota.holdemstacktracker.ui.compose.row.UserEditRow
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.SideSpace


@Composable
fun TablePrepareContent(
    uiState: TablePrepareContentUiState,
    getTableQrPainter: () -> Painter?,
    onClickDeletePlayerButton: () -> Unit,
    onClickStackEditButton: (PlayerId, String) -> Unit,
    onClickUpButton: (PlayerId) -> Unit,
    onClickDownButton: (PlayerId) -> Unit,
    onChangeBtnChosen: (PlayerId?) -> Unit,
    onClickSubmitButton: () -> Unit
) {
    val delayState = rememberDelayState()
    val qrPainter = getTableQrPainter()
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .safeDrawingPadding()
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                    ) {
                        Text(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .padding(vertical = 2.dp, horizontal = 8.dp),
                            text = stringResource(uiState.tableStatus.labelResId()),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(vertical = 2.dp, horizontal = 8.dp),
                        text = stringResource(uiState.gameTypeTextResId),
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    BlindTextLabel(
                        blindText = uiState.blindText,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = SideSpace),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.member_title_label),
                        style = MaterialTheme.typography.titleLarge
                    )

                    if (uiState.isEditable) {
                        IconButton(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = dropRedundantEvent(delayState = delayState) {
                                onClickDeletePlayerButton()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "edit"
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .weight(1.0f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "person_pin"
                        )
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = uiState.playerSizeText
                        )
                    }
                }
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
                            onClickStackEditButton = dropRedundantEvent(delayState = delayState) {
                                onClickStackEditButton(
                                    playerEditRowUiState.playerId,
                                    playerEditRowUiState.stackSize
                                )
                            },
                            onClickUpButton = dropRedundantEvent(delayState = delayState) {
                                onClickUpButton(playerEditRowUiState.playerId)
                            },
                            onClickDownButton = dropRedundantEvent(delayState = delayState) {
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

                    uiState.btnChosenUiStateList.forEach { item ->
                        when (item) {
                            is BtnChosenUiState.BtnChosenRandom -> {
                                RadioButtonRow(
                                    item = item,
                                    isSelected = item.isSelected, // TODO
                                    labelString = { stringResource(R.string.btn_random) },
                                    onClickBtnRadioButton = {
                                        onChangeBtnChosen(null)
                                    }
                                )
                            }

                            is BtnChosenUiState.Player -> {
                                RadioButtonRow(
                                    item = item,
                                    isSelected = item.isSelected,
                                    labelString = { it.name },
                                    onClickBtnRadioButton = {
                                        onChangeBtnChosen(it.id)
                                    }
                                )
                            }
                        }
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
                        onClick = dropRedundantEvent { onClickSubmitButton() },
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


data class TablePrepareContentUiState(
    val tableId: TableId,
    val tableStatus: TableStatus,
    @StringRes
    val gameTypeTextResId: Int,
    val blindText: String,
    val playerSizeText: String,
    val playerEditRows: List<PlayerEditRowUiState>,
    val btnChosenUiStateList: List<BtnChosenUiState>,
    val enableSubmitButton: Boolean,
    val isEditable: Boolean
) {
    sealed interface BtnChosenUiState {
        val isSelected: Boolean

        data class BtnChosenRandom(
            override val isSelected: Boolean = false
        ) : BtnChosenUiState

        data class Player(
            val id: PlayerId,
            val name: String,
            override val isSelected: Boolean = false
        ) : BtnChosenUiState
    }
}


private class PreviewParam : PreviewParameterProvider<TablePrepareContentUiState> {
    override val values: Sequence<TablePrepareContentUiState> = sequenceOf(
        TablePrepareContentUiState(
            tableId = TableId("tableId"),
            tableStatus = TableStatus.PLAYING,
            gameTypeTextResId = R.string.game_type_ring,
            blindText = "100/200",
            playerSizeText = "5/10",
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isEditable = false
                )
            },
            btnChosenUiStateList = emptyList(),
            enableSubmitButton = true,
            isEditable = false
        ),
        TablePrepareContentUiState(
            tableId = TableId("tableId"),
            tableStatus = TableStatus.PREPARING,
            gameTypeTextResId = R.string.game_type_ring,
            blindText = "1/2",
            playerSizeText = "2/10",
            playerEditRows = (0..1).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isEditable = true
                )
            },
            btnChosenUiStateList = listOf(
                BtnChosenUiState.BtnChosenRandom(isSelected = false)
            ) + (0..4).map {
                BtnChosenUiState.Player(
                    id = PlayerId("playerId$it"),
                    name = "PlayerName$it",
                    isSelected = it == 0
                )
            },
            enableSubmitButton = true,
            isEditable = true
        )
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun TablePrepareContentPreview(
    @PreviewParameter(PreviewParam::class) uiState: TablePrepareContentUiState
) {
    val painter = painterResource(R.drawable.baseline_qr_code_2_24)
    HoldemStackTrackerTheme {
        TablePrepareContent(
            uiState = uiState,
            getTableQrPainter = { painter },
            onClickDeletePlayerButton = {},
            onClickStackEditButton = { _, _ -> },
            onClickUpButton = {},
            onClickDownButton = {},
            onChangeBtnChosen = {},
            onClickSubmitButton = {}
        )
    }
}