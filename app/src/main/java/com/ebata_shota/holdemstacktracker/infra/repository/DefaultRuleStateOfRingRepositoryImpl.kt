package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
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
    override val ringGameFlow: Flow<RuleState.RingGame> = combine(
        prefRepository.defaultBetViewMode,
        prefRepository.defaultSizeOfSbOfNumberMode,
        prefRepository.defaultSizeOfSbOfBbMode,
        prefRepository.defaultSizeOfBbOfNumberMode,
        prefRepository.defaultStackSizeOfNumberMode,
        prefRepository.defaultStackSizeOfBbMode
    ) {
        val defaultBetViewMode = it[0] as BetViewMode
        val defaultSizeOfSbOfNumberMode = it[1] as Int
        val defaultSizeOfSbOfBbMode = it[2] as Double
        val defaultSizeOfBbOfNumberMode = it[3] as Int
        val defaultStackSizeOfNumberMode = it[4] as Int
        val defaultStackSizeOfBBMode = it[5] as Double
        RuleState.RingGame(
            sbSize = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultSizeOfSbOfNumberMode.toDouble()
                BetViewMode.BB -> defaultSizeOfSbOfBbMode
            },
            bbSize = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultSizeOfBbOfNumberMode.toDouble()
                BetViewMode.BB -> 1.0
            },
            betViewMode = defaultBetViewMode,
            defaultStack = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultStackSizeOfNumberMode.toDouble()
                BetViewMode.BB -> defaultStackSizeOfBBMode
            }
        )
    }

    override suspend fun setDefaultBetViewMode(value: BetViewMode) {
        prefRepository.saveDefaultBetViewMode(value)
    }

    override suspend fun setDefaultSizeOfSbOfNumberMode(value: Int) {
        prefRepository.saveDefaultSizeOfSbOfNumberMode(value)
    }

    override suspend fun setDefaultSizeOfSbOfBbMode(value: Double) {
        prefRepository.saveDefaultSizeOfSbOfBbMode(value)
    }

    override suspend fun saveDefaultSizeOfBbOfNumberMode(value: Int) {
        prefRepository.saveDefaultSizeOfBbOfNumberMode(value)
    }

    override suspend fun setDefaultStackSizeOfNumberMode(value: Int) {
        prefRepository.saveDefaultStackSizeOfNumberMode(value)
    }

    override suspend fun setDefaultStackSizeOfBbMode(value: Double) {
        prefRepository.saveDefaultStackSizeOfBBMode(value)
    }
}