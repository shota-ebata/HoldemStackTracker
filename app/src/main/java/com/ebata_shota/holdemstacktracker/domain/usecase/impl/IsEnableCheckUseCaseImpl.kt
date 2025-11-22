package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsEnableCheckUseCase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsEnableCheckUseCaseImpl
@Inject
constructor(
    private val getMaxBetSizeUseCase: GetMaxBetSizeUseCase,
    private val getPendingBetPerPlayerUseCase: GetPendingBetPerPlayerUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : IsEnableCheckUseCase {
    override suspend fun invoke(
        game: Game,
        myPlayerId: PlayerId,
    ): Boolean? = withContext(dispatcher) {
        val betPhase: BetPhase = (game.phaseList.lastOrNull() as? BetPhase)
            ?: return@withContext null
        val playerOrder: List<PlayerId> = game.playerOrder
        val actionStateList: List<BetPhaseAction> = betPhase.actionStateList
        val maxBetSize = getMaxBetSizeUseCase.invoke(actionStateList)
        val pendingBetPerPlayer = getPendingBetPerPlayerUseCase.invoke(
            playerOrder = playerOrder,
            actionStateList = actionStateList
        )
        val myPendingBetSize: Int = pendingBetPerPlayer[myPlayerId] ?: 0
        return@withContext maxBetSize == myPendingBetSize
    }
}