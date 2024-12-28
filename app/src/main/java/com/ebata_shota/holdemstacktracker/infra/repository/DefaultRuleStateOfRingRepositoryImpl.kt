package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DefaultRuleStateOfRingRepositoryImpl
@Inject
constructor(
    private val prefRepository: PrefRepository
) : DefaultRuleStateOfRingRepository {
    override val ringGameFlow: Flow<Rule.RingGame> = combine(
        prefRepository.defaultSizeOfSb,
        prefRepository.defaultSizeOfBb,
        prefRepository.defaultStackSize,
    ) { defaultSizeOfSb, defaultSizeOfBb, defaultStackSize ->
        Rule.RingGame(
            sbSize = defaultSizeOfSb,
            bbSize = defaultSizeOfBb,
            defaultStack = defaultStackSize
        )
    }

    override suspend fun setDefaultBetViewMode(value: BetViewMode) {
        prefRepository.saveDefaultBetViewMode(value)
    }

    override suspend fun setDefaultSizeOfSb(value: Int) {
        prefRepository.saveDefaultSizeOfSb(value)
    }

    override suspend fun saveDefaultSizeOfBb(value: Int) {
        prefRepository.saveDefaultSizeOfBb(value)
    }

    override suspend fun setDefaultStackSize(value: Int) {
        prefRepository.saveDefaultStackSize(value)
    }
}