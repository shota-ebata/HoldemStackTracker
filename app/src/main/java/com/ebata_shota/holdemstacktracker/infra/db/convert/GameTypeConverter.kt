package com.ebata_shota.holdemstacktracker.infra.db.convert

import androidx.room.TypeConverter
import com.ebata_shota.holdemstacktracker.domain.model.GameType

class GameTypeConverter {
    @TypeConverter
    fun fromGameType(gameType: GameType): Int {
        return gameType.ordinal
    }

    @TypeConverter
    fun toGameType(ordinal: Int): GameType {
        return GameType.entries[ordinal]
    }
}