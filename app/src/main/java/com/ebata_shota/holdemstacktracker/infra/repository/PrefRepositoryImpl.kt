package com.ebata_shota.holdemstacktracker.infra.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultBetViewMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfBbOfNumberMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfSbOfBbMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfSbOfNumberMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultStackSizeOfBbMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultStackSizeOfNumberMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.EnableRaiseUpSliderStep
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.MyName
import com.ebata_shota.holdemstacktracker.infra.extension.prefFlow
import com.ebata_shota.holdemstacktracker.infra.extension.setPrefValue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PrefRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
    private val randomIdRepository: RandomIdRepository
) : PrefRepository {

    override val myName: Flow<String?> by dataStore.prefFlow(
        key = MyName,
//        defaultValue = { "Player" + randomIdRepository.generateRandomId().take(6) }
        defaultValue = { null }
    )

    override suspend fun saveMyName(myName: String) {
        dataStore.setPrefValue(MyName, myName)
    }

    override val defaultBetViewMode: Flow<BetViewMode> by dataStore.prefFlow(
        key = DefaultBetViewMode,
        mapToModel = { BetViewMode.entries[it] },
        defaultValue = { BetViewMode.Number }
    )

    override suspend fun saveDefaultBetViewMode(betViewMode: BetViewMode) {
        dataStore.setPrefValue(DefaultBetViewMode, betViewMode.ordinal)
    }

    override val defaultSizeOfSbOfNumberMode: Flow<Int> by dataStore.prefFlow(
        key = DefaultSizeOfSbOfNumberMode,
        defaultValue = { 1 }
    )

    override suspend fun saveDefaultSizeOfSbOfNumberMode(value: Int) {
        dataStore.setPrefValue(DefaultSizeOfSbOfNumberMode, value)
    }

    override val defaultSizeOfSbOfBbMode: Flow<Double> by dataStore.prefFlow(
        key = DefaultSizeOfSbOfBbMode,
        defaultValue = { 0.5 }
    )

    override suspend fun saveDefaultSizeOfSbOfBbMode(value: Double) {
        dataStore.setPrefValue(DefaultSizeOfSbOfBbMode, value)
    }

    override val defaultSizeOfBbOfNumberMode: Flow<Int> by dataStore.prefFlow(
        key = DefaultSizeOfBbOfNumberMode,
        defaultValue = { 2 }
    )

    override suspend fun saveDefaultSizeOfBbOfNumberMode(value: Int) {
        dataStore.setPrefValue(DefaultSizeOfBbOfNumberMode, value)
    }

    override val defaultStackSizeOfNumberMode: Flow<Int> by dataStore.prefFlow(
        key = DefaultStackSizeOfNumberMode,
        defaultValue = { 200 }
    )

    override suspend fun saveDefaultStackSizeOfNumberMode(value: Int) {
        dataStore.setPrefValue(DefaultStackSizeOfNumberMode, value)
    }

    override val defaultStackSizeOfBbMode: Flow<Double> by dataStore.prefFlow(
        key = DefaultStackSizeOfBbMode,
        defaultValue = { 50.0 }
    )

    override suspend fun saveDefaultStackSizeOfBBMode(value: Double) {
        dataStore.setPrefValue(DefaultStackSizeOfBbMode, value)
    }

    override val isEnableRaiseUpSliderStep: Flow<Boolean> by dataStore.prefFlow(
        key = EnableRaiseUpSliderStep,
        defaultValue = { true }
    )

    override suspend fun saveEnableRaiseUpSliderStep(value: Boolean) {
        dataStore.setPrefValue(EnableRaiseUpSliderStep, value)
    }
}
