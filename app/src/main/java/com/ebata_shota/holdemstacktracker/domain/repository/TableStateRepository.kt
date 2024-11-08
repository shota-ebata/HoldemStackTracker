package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import kotlinx.coroutines.flow.Flow

interface TableStateRepository {
    fun getTableStateFlow(tableId: Long): Flow<GameState>
    suspend fun setNewGameState(tableId: TableId, newGameState: GameState)
}