package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val gameFlow: Flow<Game>

    suspend fun sendGame(
        tableId: TableId,
        newGame: Game
    )

    fun startCollectGameFlow(tableId: TableId)
    fun stopCollectGameFlow()
}