package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableId
import java.util.HashMap

interface RealtimeDatabaseRepository {
    suspend fun setGameHashMap(
        tableId: TableId,
        gameHashMap: HashMap<String, Any>
    )
}