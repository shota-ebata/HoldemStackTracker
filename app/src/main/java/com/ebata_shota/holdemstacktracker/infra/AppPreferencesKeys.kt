package com.ebata_shota.holdemstacktracker.infra

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val MyPlayerId = stringPreferencesKey("my_player_id")
    val MyName = stringPreferencesKey("my_name")
    val DefaultBetViewMode = intPreferencesKey("default_bet_view_mode")
    val DefaultSizeOfSB = doublePreferencesKey("default_size_of_sb")
    val DefaultSizeOfBB = doublePreferencesKey("default_size_of_bb")
    val DefaultStackSizeOfNumberMode = doublePreferencesKey("default_stack_size_of_number_mode")
    val DefaultStackSizeOfBBMode = doublePreferencesKey("default_stack_size_of_bb_mode")
}
