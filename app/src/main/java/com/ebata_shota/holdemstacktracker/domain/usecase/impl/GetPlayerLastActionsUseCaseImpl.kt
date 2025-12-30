package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPlayerLastActionsUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActionUseCase: GetPlayerLastActionUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPlayerLastActionsUseCase {
    /**
     * @return プレイヤーそれぞれの最後のActionのMap
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): Map<PlayerId, BetPhaseAction?> = withContext(dispatcher) {
        return@withContext playerOrder.associateWith { playerId ->
            getPlayerLastActionUseCase.invoke(
                playerId = playerId,
                phaseList = phaseList
            )
        }
    }
}