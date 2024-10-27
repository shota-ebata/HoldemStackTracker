package com.ebata_shota.holdemstacktracker.viewmodel

import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class TableViewModel(
    private val tableStateRepo: TableStateRepository
) : ViewModel() {
    
}