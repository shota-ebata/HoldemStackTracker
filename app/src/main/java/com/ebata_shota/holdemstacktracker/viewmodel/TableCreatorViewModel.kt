package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableCreatorViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val tableStateRepository: TableStateRepository,
    private val gameStateRepository: GameStateRepository,
    private val randomIdRepository: RandomIdRepository
) : ViewModel() {

    private fun createTable() {
        viewModelScope.launch {
            val tableId = TableId(randomIdRepository.generateRandomId())
            // TODO: いろいろ
            tableStateRepository.createNewTable(
                tableId = tableId,
                tableName = "テーブルねーむ",
                ruleState = RuleState.LingGame(
                    sbSize = 100.0,
                    bbSize = 200.0,
                    betViewMode = BetViewMode.Number,
                    defaultStack = 1000.0
                )
            )
            // collectを開始
            tableStateRepository.startCollectTableFlow(tableId)
            gameStateRepository.startCollectGameFlow(tableId)
        }
    }
}