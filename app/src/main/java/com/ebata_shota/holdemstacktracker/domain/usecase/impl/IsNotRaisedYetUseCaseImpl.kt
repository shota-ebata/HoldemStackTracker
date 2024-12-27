package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsNotRaisedYetUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : IsNotRaisedYetUseCase {

    /**
     * まだRaise(引き上げ)をしていない
     */
    override suspend fun invoke(actionStateList: List<BetPhaseAction>): Boolean =
        withContext(dispatcher) {
            return@withContext !actionStateList.any {
            it is BetPhaseAction.Bet
                    || it is BetPhaseAction.AllIn // AllInから入る可能性もあるので
        }
    }
}