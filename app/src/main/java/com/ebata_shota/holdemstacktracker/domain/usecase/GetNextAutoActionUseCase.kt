package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

fun interface GetNextAutoActionUseCase {

    suspend operator fun invoke(
        actionPlayerId: PlayerId,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
        game: Game,
    ): BetPhaseAction?
}
