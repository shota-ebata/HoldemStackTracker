package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

interface GetAddedAutoActionsGameUseCase {
    suspend fun invoke(
        game: Game,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
    ): Game
}