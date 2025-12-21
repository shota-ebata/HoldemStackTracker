package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.BanPlayersUseCase
import javax.inject.Inject

class BanPlayersUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : BanPlayersUseCase {
    override suspend fun invoke(
        currentTable: Table,
        banPlayerIds: List<PlayerId>,
    ) {
        val removedPlayerOrder = currentTable.playerOrder.filterNot { playerId ->
            banPlayerIds.any { it == playerId }
        }

        tableRepository.addBanPlayers(
            tableId = currentTable.id,
            newPlayerOrder = removedPlayerOrder,
            banPlayerIds = banPlayerIds,
        )
    }
}
