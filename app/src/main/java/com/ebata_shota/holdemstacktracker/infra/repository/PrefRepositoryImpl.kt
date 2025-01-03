package com.ebata_shota.holdemstacktracker.infra.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultBetViewMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfBb
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfSb
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultStackSize
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.EnableRaiseUpSliderStep
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.KeepScreenOn
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.MyName
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.PotSliderMaxRatio
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

    override val defaultSizeOfSb: Flow<Int> by dataStore.prefFlow(
        key = DefaultSizeOfSb,
        defaultValue = { 1 }
    )

    override suspend fun saveDefaultSizeOfSb(value: Int) {
        dataStore.setPrefValue(DefaultSizeOfSb, value)
    }

    override val defaultSizeOfBb: Flow<Int> by dataStore.prefFlow(
        key = DefaultSizeOfBb,
        defaultValue = { 2 }
    )

    override suspend fun saveDefaultSizeOfBb(value: Int) {
        dataStore.setPrefValue(DefaultSizeOfBb, value)
    }

    override val defaultStackSize: Flow<Int> by dataStore.prefFlow(
        key = DefaultStackSize,
        defaultValue = { 200 }
    )

    override suspend fun saveDefaultStackSize(value: Int) {
        dataStore.setPrefValue(DefaultStackSize, value)
    }

    override val isEnableRaiseUpSliderStep: Flow<Boolean> by dataStore.prefFlow(
        key = EnableRaiseUpSliderStep,
        defaultValue = { true }
    )

    override suspend fun saveEnableRaiseUpSliderStep(value: Boolean) {
        dataStore.setPrefValue(EnableRaiseUpSliderStep, value)
    }

    override val potSliderMaxRatio: Flow<Int> by dataStore.prefFlow(
        key = PotSliderMaxRatio,
        defaultValue = { 2 }
    )

    override suspend fun savePotSliderMaxRatio(value: Int) {
        dataStore.setPrefValue(PotSliderMaxRatio, value)
    }

    override val isKeepScreenOn: Flow<Boolean> by dataStore.prefFlow(
        key = KeepScreenOn,
        defaultValue = { false }
    )

    override suspend fun saveKeepScreenOn(value: Boolean) {
        dataStore.setPrefValue(KeepScreenOn, value)
    }
}
