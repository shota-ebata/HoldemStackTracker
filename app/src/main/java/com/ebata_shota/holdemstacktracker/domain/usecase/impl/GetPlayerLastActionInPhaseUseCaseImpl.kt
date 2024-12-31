package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionInPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPlayerLastActionInPhaseUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetPlayerLastActionInPhaseUseCase {

    /**
     * @return プレイヤーの最後のAction(このフェーズだけ）
     */
    override suspend fun invoke(
        playerId: PlayerId,
        actionList: List<BetPhaseAction>,
    ): BetPhaseAction? = withContext(dispatcher) {
        val playerAllActions = actionList.filter { it.playerId == playerId }
        // そのプレイヤーの最後のアクションを確認
        return@withContext playerAllActions.lastOrNull()
    }
}