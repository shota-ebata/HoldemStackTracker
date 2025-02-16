package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface SeatOutPlayersUseCase {

    suspend fun invoke(
        currentTable: Table,
        seatOutPlayers: List<PlayerId>,
    )
}