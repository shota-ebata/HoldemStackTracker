package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameTableInfoDetailContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreenUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GameTableInfoDetailContentUiStateMapper
@Inject
constructor() {
    fun createUiState(
        game: Game,
        gameScreenUiState: GameScreenUiState.Content,
    ): GameTableInfoDetailContentUiState {
        return GameTableInfoDetailContentUiState(
            tableId = game.tableId,
            blindText = gameScreenUiState.contentUiState.gameMainPanelUiState.centerPanelContentUiState.blindText,
            potList = game.potList.mapIndexed { index, pot ->
                GameTableInfoDetailContentUiState.Pot(
                    id = pot.id,
                    potName = if (index == 0) {
                        StringSource(R.string.label_main_pot)
                    } else {
                        StringSource(R.string.label_side_pot, pot.potNumber)
                    },
                    potSize = StringSource(pot.potSize.toString())
                )
            },
            pendingTotalBetSize = gameScreenUiState.contentUiState.gameMainPanelUiState.centerPanelContentUiState.pendingTotalBetSize
        )
    }
}