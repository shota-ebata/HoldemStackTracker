package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

fun interface BanPlayersUseCase {
    suspend operator fun invoke(
        currentTable: Table,
        banPlayerIds: List<PlayerId>
    )
}
