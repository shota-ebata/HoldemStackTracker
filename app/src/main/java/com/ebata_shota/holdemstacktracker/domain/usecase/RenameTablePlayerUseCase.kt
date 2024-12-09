package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface RenameTablePlayerUseCase {
    suspend fun invoke(
        table: Table,
        playerId: PlayerId,
        name: String
    )
}