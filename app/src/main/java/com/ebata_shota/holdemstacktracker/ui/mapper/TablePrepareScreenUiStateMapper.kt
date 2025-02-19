package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.infra.extension.blindText
import com.ebata_shota.holdemstacktracker.infra.extension.gameTextResId
import com.ebata_shota.holdemstacktracker.ui.compose.content.TablePrepareContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreenUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class TablePrepareScreenUiStateMapper
@Inject
constructor() {
    suspend fun createUiState(
        table: Table,
        myPlayerId: PlayerId,
        isNewGame: Boolean,
        btnPlayerId: PlayerId?,
    ): TablePrepareScreenUiState.Content {
        val isHost = table.hostPlayerId == myPlayerId
        return TablePrepareScreenUiState.Content(
            contentUiState = TablePrepareContentUiState(
                tableId = table.id,
                tableStatus = table.tableStatus,
                gameTypeTextResId = table.rule.gameTextResId(),
                blindText = table.rule.blindText(),
                playerSizeText = "${table.playerOrder.size}/10", // FIXME: ハードコーディングしている
                // プレイヤー一覧
                playerEditRows = table.playerOrder.mapNotNull { playerId ->
                    val player = table.basePlayers.find { it.id == playerId }
                        ?: return@mapNotNull null

                    val playerStackString = player.stack.toString() // FIXME: "%,d"にフォーマットしたかったが、ダイアログのEditTextに影響するのでやめた（どうにかしたい)
                    PlayerEditRowUiState(
                        playerId = playerId,
                        playerName = player.name,
                        stackSize = playerStackString,
                        isLeaved = player.isLeaved,
                        isEditable = isHost
                    )
                },
                enableSubmitButton = table.playerOrderWithoutLeaved.size >= 2,
                isEditable = isHost,
                btnPlayerName = btnPlayerId?.let {
                    table.basePlayersWithoutLeaved.find { it.id == btnPlayerId }?.name?.let {
                        StringSource(it)
                    }
                } ?: StringSource(R.string.btn_random),
                submitButtonText = StringSource(
                    if (isNewGame) {
                        R.string.start_game
                    } else {
                        R.string.start_next_game
                    }
                ),
            )
        )
    }
}