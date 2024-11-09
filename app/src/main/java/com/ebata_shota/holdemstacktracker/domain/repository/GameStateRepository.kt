package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import kotlinx.coroutines.flow.Flow

interface GameStateRepository {
    val gameStateFlow: Flow<GameState>

    suspend fun setGameHashMap(
        tableId: TableId,
        newGameState: GameState
    )

    fun startCollectGameStateFlow(tableId: TableId)
}