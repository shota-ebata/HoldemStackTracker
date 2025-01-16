package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface AddBetPhaseActionInToGameUseCase {

    suspend fun invoke(
        currentGame: Game,
        betPhaseAction: BetPhaseAction,
        playerOrder: List<PlayerId>,
    ): Game
}