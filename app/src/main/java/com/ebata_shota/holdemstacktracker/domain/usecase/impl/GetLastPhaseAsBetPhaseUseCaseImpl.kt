package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * FIXME: 拡張関数でいいかも
 */
class GetLastPhaseAsBetPhaseUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetLastPhaseAsBetPhaseUseCase {

    override suspend fun invoke(
        phaseList: List<Phase>,
    ): BetPhase = withContext(dispatcher) {
        val latestPhase: Phase? = phaseList.lastOrNull()
        if (latestPhase == null || latestPhase !is BetPhase) {
            throw IllegalStateException("BetPhase以外は想定外")
        }
        return@withContext latestPhase
    }
}