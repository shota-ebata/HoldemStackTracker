package com.ebata_shota.holdemstacktracker.infra

import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val MyPlayerId = stringPreferencesKey("my_player_id")
    val MyName = stringPreferencesKey("my_name")
    val DefaultBetViewMode = intPreferencesKey("default_bet_view_mode")
    val DefaultSizeOfSbOfNumberMode = intPreferencesKey("default_size_of_sb_of_number_mode")
    val DefaultSizeOfSbOfBbMode = doublePreferencesKey("default_size_of_sb_of_bb_mode")
    val DefaultSizeOfBbOfNumberMode = intPreferencesKey("default_size_of_bb")
    val DefaultStackSizeOfNumberMode = intPreferencesKey("default_stack_size_of_number_mode")
    val DefaultStackSizeOfBbMode = doublePreferencesKey("default_stack_size_of_bb_mode")
}
