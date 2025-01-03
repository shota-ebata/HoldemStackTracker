package com.ebata_shota.holdemstacktracker.infra

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferencesKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val MyPlayerId = stringPreferencesKey("my_player_id")
    val MyName = stringPreferencesKey("my_name")
    val DefaultBetViewMode = intPreferencesKey("default_bet_view_mode")
    val DefaultSizeOfSb = intPreferencesKey("default_size_of_sb")
    val DefaultSizeOfBb = intPreferencesKey("default_size_of_bb")
    val DefaultStackSize = intPreferencesKey("default_stack_size")
    val EnableRaiseUpSliderStep = booleanPreferencesKey("enable_slider_step")
    val PotSliderMaxRatio = intPreferencesKey("pot_slider_max_ratio")
    val KeepScreenOn = booleanPreferencesKey("keep_screen_on")
}
