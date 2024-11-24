package com.ebata_shota.holdemstacktracker.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ebata_shota.holdemstacktracker.ui.compose.content.MainContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val mainContentUiState: MainContentUiState?
        get() = (uiState.value as? MainUiState.Succeeded)?.mainContentUiState


    private val _navigateEvent = MutableSharedFlow<Unit>()
    val navigateEvent = _navigateEvent.asSharedFlow()

    fun onChangeText(value: TextFieldValue) {
        val mainContentUiState = mainContentUiState
            ?: return
        _uiState.update {
            MainUiState.Succeeded(
                mainContentUiState.copy(textFieldValue = value)
            )
        }
    }

}