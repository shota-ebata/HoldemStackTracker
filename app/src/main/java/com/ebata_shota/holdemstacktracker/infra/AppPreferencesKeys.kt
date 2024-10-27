package com.ebata_shota.holdemstacktracker.infra

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val PlayerId = stringPreferencesKey("")
}
