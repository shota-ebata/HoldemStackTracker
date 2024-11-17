package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import kotlinx.coroutines.flow.Flow


interface DefaultRingGameStateRepository {
    val ringGameFlow: Flow<RuleState.RingGame>

    suspend fun setDefaultBetViewMode(value: BetViewMode)
    suspend fun setDefaultSizeOfSB(value: Double)
    suspend fun setDefaultSizeOfBB(value: Double)
    suspend fun setDefaultStackSizeOfNumberMode(value: Double)
    suspend fun setDefaultStackSizeOfBBMode(value: Double)
}