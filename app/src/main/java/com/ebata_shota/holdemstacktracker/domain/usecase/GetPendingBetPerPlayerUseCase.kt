package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetPendingBetPerPlayerUseCase {
    /**
     * プレイヤーごとの、まだポットに入っていないベット額
     */
    suspend operator fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseAction>
    ): Map<PlayerId, Int>
}
