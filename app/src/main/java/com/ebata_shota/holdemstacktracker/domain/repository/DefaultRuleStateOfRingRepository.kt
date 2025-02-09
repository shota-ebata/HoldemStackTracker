package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import kotlinx.coroutines.flow.Flow


interface DefaultRuleStateOfRingRepository {
    val ringGameFlow: Flow<Rule.RingGame>

    suspend fun setDefaultBetViewMode(value: BetViewMode)
    suspend fun setDefaultSizeOfSb(value: Int)
    suspend fun setDefaultSizeOfBb(value: Int)
    suspend fun setDefaultStackSize(value: Int)
}