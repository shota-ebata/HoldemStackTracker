package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface GetNextBtnPlayerIdUseCase {
    fun invoke(
        table: Table,
        game: Game,
    ): PlayerId?
}