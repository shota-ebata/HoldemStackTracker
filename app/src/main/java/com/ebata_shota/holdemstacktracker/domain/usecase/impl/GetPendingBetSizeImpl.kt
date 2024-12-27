package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSize
import javax.inject.Inject

class GetPendingBetSizeImpl
@Inject
constructor(
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
) : GetPendingBetSize {
    /**
     * 特定の一人の現在Betしていて
     * まだPodに入っていないサイズを取得する
     */
    override fun invoke(
        actionList: List<BetPhaseAction>,
        playerOrder: List<PlayerId>,
        playerId: PlayerId,
    ): Double {
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = actionList
        )
        return pendingBetPerPlayer[playerId] ?: 0.0
    }
}