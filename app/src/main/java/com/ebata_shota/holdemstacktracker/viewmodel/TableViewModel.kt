package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextTableStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TableViewModel
@Inject
constructor(
    private val tableStateRepo: TableStateRepository,
    private val getNextTableStateUseCase: GetNextTableStateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentTableState get() = tableStateRepo.getTableStateFlow(
        tableId = 0L // TODO: savedStateから
    )

    fun onCreate() {
        tableStateRepo.test()
    }

    suspend fun setAction(action: ActionState) {
        val updatedTableState = getNextTableStateUseCase.invoke(
            latestTableState = currentTableState.first(),
            action = action
        )
        tableStateRepo.setTableState(updatedTableState)
    }
}