package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetNextPlayerStackUseCase {

    suspend fun invoke(
        latestGame: Game,
        action: BetPhaseAction,
    ): List<GamePlayer>
}