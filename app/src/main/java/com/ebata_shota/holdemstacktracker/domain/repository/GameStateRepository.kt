package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import kotlinx.coroutines.flow.Flow

interface GameStateRepository {
    val gameFlow: Flow<GameState>

    suspend fun sendGameState(
        tableId: TableId,
        newGameState: GameState
    )

    fun startCollectGameFlow(tableId: TableId)
    fun stopCollectGameFlow()
}