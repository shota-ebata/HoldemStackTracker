package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import kotlinx.coroutines.flow.Flow

interface TableStateRepository {
    fun getTableStateFlow(tableId: Long): Flow<GameState>
    suspend fun setTableState(newGameState: GameState)
}