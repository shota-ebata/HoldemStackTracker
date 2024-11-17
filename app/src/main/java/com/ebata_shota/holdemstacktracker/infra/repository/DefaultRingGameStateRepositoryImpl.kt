package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRingGameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DefaultRingGameStateRepositoryImpl
@Inject
constructor(
    private val prefRepository: PrefRepository
) : DefaultRingGameStateRepository {
    override val ringGameFlow: Flow<RuleState.RingGame> = combine(
        prefRepository.defaultBetViewMode,
        prefRepository.defaultSizeOfSB,
        prefRepository.defaultSizeOfBB,
        prefRepository.defaultStackSizeOfNumberMode,
        prefRepository.defaultStackSizeOfBBMode
    ) { defaultBetViewMode, defaultSizeOfSB, defaultSizeOfBB, defaultStackSizeOfNumberMode, defaultStackSizeOfBBMode ->
        RuleState.RingGame(
            sbSize = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultSizeOfSB
                BetViewMode.BB -> 0.5
            },
            bbSize = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultSizeOfBB
                BetViewMode.BB -> 1.0
            },
            betViewMode = defaultBetViewMode,
            defaultStack = when (defaultBetViewMode) {
                BetViewMode.Number -> defaultStackSizeOfNumberMode
                BetViewMode.BB -> defaultStackSizeOfBBMode
            }
        )
    }

    override suspend fun setDefaultBetViewMode(value: BetViewMode) {
        prefRepository.saveDefaultBetViewMode(value)
    }

    override suspend fun setDefaultSizeOfSB(value: Double) {
        prefRepository.saveDefaultSizeOfSB(value)
    }

    override suspend fun setDefaultSizeOfBB(value: Double) {
        prefRepository.saveDefaultSizeOfBB(value)
    }

    override suspend fun setDefaultStackSizeOfNumberMode(value: Double) {
        prefRepository.saveDefaultStackSizeOfNumberMode(value)
    }

    override suspend fun setDefaultStackSizeOfBBMode(value: Double) {
        prefRepository.saveDefaultStackSizeOfBBMode(value)
    }
}