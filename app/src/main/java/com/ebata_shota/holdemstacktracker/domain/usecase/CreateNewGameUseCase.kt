package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Table

interface CreateNewGameUseCase {
    suspend fun invoke(newTable: Table, newGame: Game)
}