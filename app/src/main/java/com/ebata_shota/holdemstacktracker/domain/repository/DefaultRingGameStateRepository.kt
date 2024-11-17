package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import kotlinx.coroutines.flow.Flow


interface DefaultRingGameStateRepository {
    val ringGameFlow: Flow<RuleState.RingGame>

    suspend fun setDefaultBetViewMode(value: BetViewMode)
    suspend fun setDefaultSizeOfSbOfNumberMode(value: Int)
    suspend fun setDefaultSizeOfSbOfBbMode(value: Double)
    suspend fun saveDefaultSizeOfBbOfNumberMode(value: Int)
    suspend fun setDefaultStackSizeOfNumberMode(value: Int)
    suspend fun setDefaultStackSizeOfBbMode(value: Double)
}