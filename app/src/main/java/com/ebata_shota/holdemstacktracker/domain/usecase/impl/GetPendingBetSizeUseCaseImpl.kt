package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPendingBetSizeUseCaseImpl
@Inject
constructor(
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPendingBetSizeUseCase {
    /**
     * 特定の一人の現在Betしていて
     * まだPotに入っていないサイズを取得する
     */
    override suspend fun invoke(
        actionList: List<BetPhaseAction>,
        playerOrder: List<PlayerId>,
        playerId: PlayerId,
    ): Int = withContext(dispatcher) {
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = actionList
        )
        return@withContext pendingBetPerPlayer[playerId] ?: 0
    }
}