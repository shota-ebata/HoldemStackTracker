package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game

fun interface AddBetPhaseActionInToGameUseCase {

    suspend operator fun invoke(
        currentGame: Game,
        betPhaseAction: BetPhaseAction,
    ): Game
}
