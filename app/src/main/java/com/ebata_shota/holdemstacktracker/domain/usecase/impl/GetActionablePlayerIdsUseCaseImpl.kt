package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionablePlayerIdsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetActionablePlayerIdsUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetActionablePlayerIdsUseCase {

    /**
     * アクションできるプレイヤーIDの一覧を取得する
     * 正確に言うと降りているわけでもなく、AllInしているわけでもない
     * アクションする権限を保持しているプレイヤーIDの一覧を取得する。
     * (フェーズを跨いだ後も考慮)
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>,
    ): List<PlayerId> = withContext(dispatcher) {

        val lastActions: Map<PlayerId, BetPhaseAction?> = getPlayerLastActions.invoke(
            playerOrder = playerOrder,
            phaseList = phaseList
        )

        return@withContext playerOrder.mapNotNull { playerId ->
            val lastAction = lastActions[playerId]
            when (lastAction) {
                is BetPhaseAction.AllInSkip,
                is BetPhaseAction.FoldSkip,
                is BetPhaseAction.AllIn,
                is BetPhaseAction.Fold,
                    -> null

                is BetPhaseAction.Blind,
                is BetPhaseAction.Bet,
                is BetPhaseAction.Call,
                is BetPhaseAction.Raise,
                is BetPhaseAction.Check,
                    -> playerId

                null -> playerId
            }
        }
    }
}