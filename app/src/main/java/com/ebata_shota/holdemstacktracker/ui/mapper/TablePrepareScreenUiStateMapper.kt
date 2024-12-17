package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
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
constructor(
    private val getDoubleToString: GetDoubleToStringUseCase
) {
    fun createUiState(
        table: Table,
        myPlayerId: PlayerId,
        btnPlayerId: PlayerId?
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

                    val playerStackString = getDoubleToString.invoke(
                        value = player.stack,
                        betViewMode = table.rule.betViewMode
                    )
                    PlayerEditRowUiState(
                        playerId = playerId,
                        playerName = player.name,
                        stackSize = playerStackString,
                        isEditable = isHost
                    )
                },
                // BTNプレイヤーの選択肢
                btnChosenUiStateList = listOf(
                    TablePrepareContentUiState.BtnChosenUiState.BtnChosenRandom(
                        isSelected = btnPlayerId == null
                    )
                ) + table.playerOrder.map { playerId ->
                    TablePrepareContentUiState.BtnChosenUiState.Player(
                        id = playerId,
                        name = table.basePlayers.find { it.id == playerId }!!.name,
                        isSelected = btnPlayerId == playerId
                    )
                },
                enableSubmitButton = table.playerOrder.size >= 2,
                isEditable = isHost
            )
        )
    }
}