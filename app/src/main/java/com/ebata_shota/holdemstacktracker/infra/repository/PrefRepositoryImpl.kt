package com.ebata_shota.holdemstacktracker.infra.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys
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
        key = AppPreferencesKeys.MyName,
        defaultValue = { "Player" + randomIdRepository.generateRandomId().take(6) }
    )

    override suspend fun saveMyName(myName: String) {
        dataStore.setPrefValue(AppPreferencesKeys.MyName, myName)
    }
}