package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

}