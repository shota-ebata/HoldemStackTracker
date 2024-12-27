package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMaxBetSizeUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetMaxBetSizeUseCase {
    override suspend fun invoke(actionStateList: List<BetPhaseAction>): Double =
        withContext(dispatcher) {
            return@withContext actionStateList.maxOfOrNull {
            if (it is BetPhaseAction.BetAction) {
                it.betSize
            } else {
                0.0
            }
        } ?: 0.0
    }
}