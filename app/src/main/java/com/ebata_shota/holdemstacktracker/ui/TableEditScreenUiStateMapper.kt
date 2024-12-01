package com.ebata_shota.holdemstacktracker.ui

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableEditContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PlayerEditRowUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreenUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class TableEditScreenUiStateMapper
@Inject
constructor(
    private val getDoubleToString: GetDoubleToStringUseCase
) {
    fun createUiState(
        table: Table,
        myPlayerId: PlayerId,
        btnPlayerId: PlayerId?
    ): TableEditScreenUiState.Content {
        val isHost = table.hostPlayerId == myPlayerId
        return TableEditScreenUiState.Content(
            contentUiState = TableEditContentUiState(
                tableId = table.id,
                playerEditRows = table.playerOrder.mapNotNull { playerId ->
                    val player = table.basePlayers.find { it.id == playerId }
                        ?: return@mapNotNull null

                    val playerStackString = getDoubleToString.invoke(
                        value = player.stack,
                        betViewMode = table.ruleState.betViewMode
                    )
                    PlayerEditRowUiState(
                        playerId = playerId,
                        playerName = player.name,
                        stackSize = playerStackString,
                        isEditable = isHost
                    )
                },
                btnChosenUiStateList = listOf(
                    TableEditContentUiState.BtnChosenUiState.BtnChosenRandom(
                        isSelected = btnPlayerId == null
                    )
                ) + table.playerOrder.map { playerId ->
                    TableEditContentUiState.BtnChosenUiState.Player(
                        id = playerId,
                        name = table.basePlayers.find { it.id == playerId }!!.name,
                        isSelected = btnPlayerId == playerId
                    )
                },
                enableSubmitButton = table.playerOrder.size >= 2,
                isEditable = isHost
            ),
            stackEditDialogState = null
        )
    }
}