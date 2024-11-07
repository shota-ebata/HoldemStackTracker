package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TableViewModel
@Inject
constructor(
    private val tableStateRepo: TableStateRepository,
    private val getNextGameStateUseCase: GetNextGameStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentTableState = tableStateRepo.getTableStateFlow(
        tableId = TODO("savedStateから")
    )

    suspend fun setAction(
        action: ActionState,
    ) {
        val updatedTableState = getNextGameStateUseCase.invoke(
            latestGameState = currentTableState.first(),
            action = action
        )
        tableStateRepo.setTableState(updatedTableState)
    }
}