package com.ebata_shota.holdemstacktracker.infra

import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val MyPlayerId = stringPreferencesKey("my_player_id")
    val MyName = stringPreferencesKey("my_name")
}
