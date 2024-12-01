package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface MovePositionUseCase {
    suspend fun invoke(
        playerId: PlayerId,
        table: Table,
        movePosition: MovePosition
    )
}