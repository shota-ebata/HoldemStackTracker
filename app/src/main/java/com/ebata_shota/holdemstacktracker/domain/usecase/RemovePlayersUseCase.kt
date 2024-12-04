package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface RemovePlayersUseCase {
    suspend fun invoke(
        currentTable: Table,
        removePlayerIds: List<PlayerId>
    )
}