package com.ebata_shota.holdemstacktracker.infra.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultBetViewMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfBB
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultSizeOfSB
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultStackSizeOfBBMode
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys.DefaultStackSizeOfNumberMode
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

    override val myName: Flow<String> by dataStore.prefFlow(
        key = MyName,
        defaultValue = { "Player" + randomIdRepository.generateRandomId().take(6) }
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

    override val defaultSizeOfSB: Flow<Double> by dataStore.prefFlow(
        key = DefaultSizeOfSB,
        defaultValue = { 100.0 }
    )

    override suspend fun saveDefaultSizeOfSB(value: Double) {
        dataStore.setPrefValue(DefaultSizeOfSB, value)
    }

    override val defaultSizeOfBB: Flow<Double> by dataStore.prefFlow(
        key = DefaultSizeOfBB,
        defaultValue = { 200.0 }
    )

    override suspend fun saveDefaultSizeOfBB(value: Double) {
        dataStore.setPrefValue(DefaultSizeOfBB, value)
    }

    override val defaultStackSizeOfNumberMode: Flow<Double> by dataStore.prefFlow(
        key = DefaultStackSizeOfNumberMode,
        defaultValue = { 10000.0 }
    )

    override suspend fun saveDefaultStackSizeOfNumberMode(value: Double) {
        dataStore.setPrefValue(DefaultStackSizeOfNumberMode, value)
    }

    override val defaultStackSizeOfBBMode: Flow<Double> by dataStore.prefFlow(
        key = DefaultStackSizeOfBBMode,
        defaultValue = { 50.0 }
    )

    override suspend fun saveDefaultStackSizeOfBBMode(value: Double) {
        dataStore.setPrefValue(DefaultStackSizeOfBBMode, value)
    }
}
