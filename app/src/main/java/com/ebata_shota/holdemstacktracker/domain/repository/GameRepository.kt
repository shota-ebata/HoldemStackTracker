package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {
    val gameStateFlow: StateFlow<Result<Game>?>

    suspend fun sendGame(
        tableId: TableId,
        newGame: Game
    )

    fun startCollectGameFlow(tableId: TableId)
    fun stopCollectGameFlow()
}