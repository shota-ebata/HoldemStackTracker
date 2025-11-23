package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

interface DoTransitionToNextPhaseIfNeedUseCase {
    suspend fun invoke(
        game: Game,
        hostPlayerId: PlayerId,
        rule: Rule,
    )
}