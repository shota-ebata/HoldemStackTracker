package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

interface GetNextAutoActionUseCase {

    suspend fun invoke(
        actionPlayerId: PlayerId,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
        game: Game,
    ): BetPhaseAction?
}