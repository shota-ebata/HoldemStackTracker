package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.SeatOutPlayersUseCase
import javax.inject.Inject


class SeatOutPlayersUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : SeatOutPlayersUseCase {

    override suspend fun invoke(
        currentTable: Table,
        seatOutPlayers: List<PlayerId>,
    ) {
        val copiedTable = currentTable.copy(
            basePlayers = currentTable.basePlayers.map { basePlayer ->
                basePlayer.copy(
                    isLeaved = seatOutPlayers.contains(basePlayer.id)
                )
            }
        )
        tableRepository.sendTable(copiedTable)
    }
}