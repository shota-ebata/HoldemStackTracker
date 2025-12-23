package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface RequestJoinTableUseCase {
    suspend fun invoke(
        table: Table,
        myPlayerId: PlayerId,
        myName: String
    )
}