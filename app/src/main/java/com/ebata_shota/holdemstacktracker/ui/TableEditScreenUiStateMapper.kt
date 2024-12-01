package com.ebata_shota.holdemstacktracker.ui

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenUiState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableEditScreenUiStateMapper
@Inject
constructor(
    private val getDoubleToString: GetDoubleToStringUseCase
) {
    fun createUiState(
        tableState: Table,
        myPlayerId: PlayerId
    ): TableEditScreenUiState.Content {
        val isHost = tableState.hostPlayerId == myPlayerId
        return TableEditScreenUiState.Content(
            contentUiState = TableEditContentUiState(
                tableId = tableState.id,
                playerEditRows = tableState.playerOrder.mapNotNull { playerId ->
                    val player = tableState.basePlayers.find { it.id == playerId }
                        ?: return@mapNotNull null

                    val playerStackString = getDoubleToString.invoke(
                        value = player.stack,
                        betViewMode = tableState.ruleState.betViewMode
                    )
                    PlayerEditRowUiState(
                        playerId = playerId,
                        playerName = player.name,
                        stackSize = playerStackString,
                        isEditable = isHost
                    )
                },
                isAddable = isHost
            ),
            stackEditDialogState = null
        )
    }
}