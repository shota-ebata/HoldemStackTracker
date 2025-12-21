package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface BanPlayersUseCase {
    suspend fun invoke(
        currentTable: Table,
        banPlayerIds: List<PlayerId>
    )
}