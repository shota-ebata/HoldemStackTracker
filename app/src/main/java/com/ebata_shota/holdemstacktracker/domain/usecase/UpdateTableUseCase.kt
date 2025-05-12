package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Table
import java.time.Instant

interface UpdateTableUseCase {
    suspend fun invoke(
        table: Table,
        // interfaceのデフォルト引数って分かりづらいよね...
        updateTime: Instant = Instant.now(),
    )
}