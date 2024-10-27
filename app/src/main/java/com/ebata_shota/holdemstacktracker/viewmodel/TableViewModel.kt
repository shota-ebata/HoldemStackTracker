package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.domain.usecase.CurrentActionPlayerIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TableViewModel
@Inject
constructor(
//    private val tableStateRepo: TableStateRepository
    private val currentActionPlayerIdUseCase: CurrentActionPlayerIdUseCase
) : ViewModel() {

}