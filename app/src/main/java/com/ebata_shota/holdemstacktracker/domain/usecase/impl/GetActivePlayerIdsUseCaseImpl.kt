package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActivePlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetActivePlayerIdsUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetActivePlayerIdsUseCase {

    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>,
    ): List<PlayerId> = withContext(dispatcher) {
        val lastActions: Map<PlayerId, BetPhaseAction?> =
            getPlayerLastActions.invoke(playerOrder, phaseList)
        // 降りてないプレイヤー
        val activePlayers: List<PlayerId> = lastActions.filter { (_, lastAction) ->
            lastAction !is BetPhaseAction.FoldSkip && lastAction !is BetPhaseAction.Fold
        }.map { it.key }
        return@withContext activePlayers
    }
}