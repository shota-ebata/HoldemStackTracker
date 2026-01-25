package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule

fun interface ExecuteAllInUseCase {
    suspend operator fun invoke(
        currentGame: Game,
        rule: Rule,
        myPlayerId: PlayerId,
        leavedPlayerIds: List<PlayerId>,
    )
}