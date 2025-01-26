package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface AddBetPhaseActionInToGameUseCase {

    suspend fun invoke(
        playerOrder: List<PlayerId>,
        btnPlayerId: PlayerId,
        currentGame: Game,
        betPhaseAction: BetPhaseAction,
    ): Game
}