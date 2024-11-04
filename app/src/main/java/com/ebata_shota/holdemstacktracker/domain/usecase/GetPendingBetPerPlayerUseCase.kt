package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPendingBetPerPlayerUseCase {
    /**
     * プレイヤーごとの、まだポッドに入っていないベット額
     */
    fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseActionState>
    ): Map<PlayerId, Double>
}