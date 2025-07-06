package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.RemovePlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import javax.inject.Inject

class RemovePlayersUseCaseImpl
@Inject
constructor(
    private val updateTableUseCase: UpdateTableUseCase,
) : RemovePlayersUseCase {
    override suspend fun invoke(
        currentTable: Table,
        removePlayerIds: List<PlayerId>
    ) {
        val removedPlayerOrder = currentTable.playerOrder.filterNot { playerId ->
            removePlayerIds.any { it == playerId }
        }
        val copiedTable = currentTable.copy(
            playerOrder = removedPlayerOrder,
        )
        updateTableUseCase.invoke(copiedTable)
    }
}
