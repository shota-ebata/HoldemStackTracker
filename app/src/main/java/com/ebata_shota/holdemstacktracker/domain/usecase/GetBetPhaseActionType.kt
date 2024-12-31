package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType

interface GetLastBetPhaseActionTypeUseCase {

    suspend fun invoke(
        game: Game,
        playerId: PlayerId,
    ): BetPhaseActionType?
}