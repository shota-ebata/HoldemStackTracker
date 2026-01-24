package com.ebata_shota.holdemstacktracker.ui.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PotSettlementCheckboxRowUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class PotSettlementDialogUiStateMapper
@Inject
constructor(
    private val getNotFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
) {
    suspend fun createUiState(
        table: Table,
        game: Game,
    ): PotSettlementDialogUiState {
        val notFoldPlayerIds = getNotFoldPlayerIds.invoke(
            playerOrder = game.playerOrder,
            phaseList = game.phaseList
        )
        val dialogUiState = PotSettlementDialogUiState(
            currentPotIndex = 0,
            pots = game.potList.reversed().map { pot ->
                PotSettlementDialogUiState.PotUiState(
                    potId = pot.id,
                    potNumber = pot.potNumber,
                    potSizeString = StringSource(pot.potSize.toString()),
                    players = pot.involvedPlayerIds.map { involvedPlayerId ->
                        val isEnable = notFoldPlayerIds.any {
                            it == involvedPlayerId
                        }
                        PotSettlementCheckboxRowUiState(
                            playerId = involvedPlayerId,
                            playerName = StringSource(
                                table.getPlayerName(involvedPlayerId) ?: "???"
                            ),
                            isEnable = isEnable,
                            shouldShowFoldLabel = !isEnable
                        )
                    }
                )
            },
        )
        return dialogUiState
    }
}