package com.ebata_shota.holdemstacktracker.infra.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.infra.AppPreferencesKeys
import com.ebata_shota.holdemstacktracker.infra.extension.prefFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PrefRepositoryImpl
@Inject
constructor(
    private val dataStore: DataStore<Preferences>
) : PrefRepository {
    val playerId: Flow<String> by dataStore.prefFlow(
        key = AppPreferencesKeys.PlayerId,
        defaultValue = "" // TODO: ランダム生成
    )
}