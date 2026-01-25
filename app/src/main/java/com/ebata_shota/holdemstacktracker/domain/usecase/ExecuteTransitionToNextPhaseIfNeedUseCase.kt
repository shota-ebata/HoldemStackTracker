package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

fun interface ExecuteTransitionToNextPhaseIfNeedUseCase {
    suspend operator fun invoke(
        game: Game,
        hostPlayerId: PlayerId,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
    )
}
