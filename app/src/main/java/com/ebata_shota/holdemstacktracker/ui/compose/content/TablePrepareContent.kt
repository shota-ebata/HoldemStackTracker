package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.ui.compose.extension.labelResId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.BlindTextLabel
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.UserEditRow
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.SideSpace


@Composable
fun TablePrepareContent(
    uiState: TablePrepareContentUiState,
    getTableQrPainter: () -> Painter?,
    onClickEditGameRuleButton: () -> Unit,
    onClickRemovePlayerButton: () -> Unit,
    onClickPlayerEditButton: (PlayerId) -> Unit,
    onClickUpButton: (PlayerId) -> Unit,
    onClickDownButton: (PlayerId) -> Unit,
    onClickEditBtnPlayerButton: () -> Unit,
    onClickSubmitButton: () -> Unit,
) {
    val delayState = rememberDelayState()
    val qrPainter = getTableQrPainter()
    val clipboardManager = LocalClipboardManager.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .safeDrawingPadding()
                .fillMaxWidth()
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
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = uiState.tableIdText.getString(),
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        IconButton(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = dropRedundantEvent(delayState = delayState) {
                                clipboardManager.setText(AnnotatedString(uiState.tableId.value))
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_content_copy_24),
                                contentDescription = "copy"
                            )
                        }

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
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
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

                    if (uiState.isEditable) {
                        IconButton(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = dropRedundantEvent {
                                onClickEditGameRuleButton()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "edit"
                            )
                        }
                    }
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
                if (uiState.isEditable) {
                    Row(
                        Modifier
                            .padding(top = 16.dp)
                            .padding(horizontal = SideSpace)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {

                        OutlinedButton(
                            onClick = dropRedundantEvent(delayState = delayState) {
                                onClickRemovePlayerButton()
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "edit"
                            )
                            Spacer(
                                modifier = Modifier.size(ButtonDefaults.IconSpacing)
                            )
                            Text(
                                text = stringResource(R.string.button_label_remove),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    uiState.playerEditRows.forEachIndexed { index, playerEditRowUiState ->
                        UserEditRow(
                            uiState = playerEditRowUiState,
                            onClickPlayerEditButton = dropRedundantEvent(delayState = delayState) {
                                onClickPlayerEditButton(
                                    playerEditRowUiState.playerId
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
            }

            // ゲーム開始ボタン
            if (uiState.isEditable) {
                HorizontalDivider(
                    modifier = Modifier
                )
                // BTNの決め方
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SideSpace, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.btn_chosen),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1.0f, fill = false),
                        text = uiState.btnPlayerName.getString(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = dropRedundantEvent {
                            onClickEditBtnPlayerButton()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "edit"
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = dropRedundantEvent { onClickSubmitButton() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState.enableSubmitButton
                    ) {
                        Text(
                            text = uiState.submitButtonText.getString(),
                            style = MaterialTheme.typography.bodyLarge,
                        )
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
    val enableSubmitButton: Boolean,
    val isEditable: Boolean,
    val btnPlayerName: StringSource,
    val submitButtonText: StringSource,
) {
    val tableIdText = StringSource(R.string.table_id_prefix, tableId.value)
}


private class PreviewParam : PreviewParameterProvider<TablePrepareContentUiState> {
    override val values: Sequence<TablePrepareContentUiState> = sequenceOf(
        TablePrepareContentUiState(
            tableId = TableId("qwe123"),
            tableStatus = TableStatus.PLAYING,
            gameTypeTextResId = R.string.game_type_ring,
            blindText = "100/200",
            playerSizeText = "5/10",
            playerEditRows = (0..4).map {
                PlayerEditRowUiState(
                    playerId = PlayerId("playerId$it"),
                    playerName = "PlayerName$it",
                    stackSize = "10000",
                    isLeaved = true,
                    isEditable = false
                )
            },
            enableSubmitButton = true,
            isEditable = false,
            btnPlayerName = StringSource("PlayerName1"),
            submitButtonText = StringSource(R.string.start_game),
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
                    isLeaved = false,
                    isEditable = true
                )
            },
            enableSubmitButton = true,
            isEditable = true,
            btnPlayerName = StringSource("風野っｓｄｓｆｓｄｌｆ；ｊｓ； != 風野っｓｄｓｆｓｄｌｆ；ｊｓ；"),
            submitButtonText = StringSource(R.string.start_game),
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
            onClickEditGameRuleButton = {},
            onClickRemovePlayerButton = {},
            onClickPlayerEditButton = {},
            onClickUpButton = {},
            onClickDownButton = {},
            onClickEditBtnPlayerButton = {},
            onClickSubmitButton = {}
        )
    }
}