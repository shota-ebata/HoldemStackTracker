package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetNextGamePlayerStateListUseCase {
    suspend operator fun invoke(
        pendingBetPerPlayer: Map<PlayerId, Int>,
        players: List<GamePlayer>,
        action: BetPhaseAction
    ): List<GamePlayer>
}
