package com.ebata_shota.holdemstacktracker.infra.db.convert

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let {
            Instant.ofEpochMilli(value)
        }
    }

    @TypeConverter
    fun localToTimestamp(localDateTime: Instant?): Long? {
        return localDateTime?.toEpochMilli()
    }
}