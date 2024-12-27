package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLatestBetPhaseUseCaseImpl
@Inject constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetLatestBetPhaseUseCase {

    // FIXME: 引数phaseListにしたい
    override suspend fun invoke(latestGame: Game): BetPhase = withContext(dispatcher) {
        val latestPhase: Phase? = latestGame.phaseList.lastOrNull()
        if (latestPhase == null || latestPhase !is BetPhase) {
            throw IllegalStateException("BetPhase以外は想定外")
        }
        return@withContext latestPhase
    }
}