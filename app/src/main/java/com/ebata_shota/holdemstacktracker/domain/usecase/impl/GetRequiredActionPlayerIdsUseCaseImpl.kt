package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRequiredActionPlayerIdsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRequiredActionPlayerIdsUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetRequiredActionPlayerIdsUseCase {

    /**
     * アクションが必要なプレイヤーIDの一覧を取得する
     */
    override suspend fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): List<PlayerId> = withContext(dispatcher) {

        val lastActions: Map<PlayerId, BetPhaseAction?> = getPlayerLastActions.invoke(
            playerOrder = playerOrder,
            phaseList = currentGame.phaseList
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

                null -> null
            }
        }
    }
}