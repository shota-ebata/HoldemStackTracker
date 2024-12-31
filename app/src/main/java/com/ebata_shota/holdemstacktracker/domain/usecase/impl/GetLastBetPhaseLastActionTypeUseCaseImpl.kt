package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastBetPhaseActionTypeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionInPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLastBetPhaseLastActionTypeUseCaseImpl
@Inject
constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getPlayerLastAction: GetPlayerLastActionUseCase,
    private val getPlayerLastActionInPhase: GetPlayerLastActionInPhaseUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetLastBetPhaseActionTypeUseCase {

    override suspend fun invoke(
        game: Game,
        playerId: PlayerId,
    ): BetPhaseActionType? = withContext(dispatcher) {
        val lastBetPhase = try {
            getLatestBetPhase.invoke(game)
        } catch (e: IllegalStateException) {
            null
        }
        val playerLastAction = if (lastBetPhase != null) {
            // このフェーズでの最新アクション
            val playerLastActionInPhase = getPlayerLastActionInPhase.invoke(
                playerId = playerId,
                actionList = lastBetPhase.actionStateList
            )
            if (playerLastActionInPhase != null) {
                // すでにこのフェーズでアクションしていたら、それを返す
                playerLastActionInPhase
            } else {
                // このフェーズでの最新アクションが存在しないなら
                // 過去のフェーズでのFoldやAllInを見てみる
                val playerLastAction = getPlayerLastAction.invoke(
                    playerId = playerId,
                    phaseList = game.phaseList
                )
                when (playerLastAction) {
                    is BetPhaseAction.AllInSkip,
                    is BetPhaseAction.FoldSkip,
                    is BetPhaseAction.AllIn,
                    is BetPhaseAction.Fold,
                        -> playerLastAction

                    else -> null
                }
            }
        } else {
            null
        }
        return@withContext playerLastAction?.let {
            BetPhaseActionType.of(it)
        }
    }
}