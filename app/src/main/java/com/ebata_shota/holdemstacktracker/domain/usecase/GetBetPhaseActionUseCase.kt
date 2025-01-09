package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetBetPhaseActionUseCase {

    suspend fun invoke(
        game: Game,
        actionId: ActionId,
    ): BetPhaseAction?
}