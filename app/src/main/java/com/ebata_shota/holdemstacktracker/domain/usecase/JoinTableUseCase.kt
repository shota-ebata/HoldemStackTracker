package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface JoinTableUseCase {
    suspend fun invoke(
        tableState: Table,
        myPlayerId: PlayerId,
        myName: String
    )
}