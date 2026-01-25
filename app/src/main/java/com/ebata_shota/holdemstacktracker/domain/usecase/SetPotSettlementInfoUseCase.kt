package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState

fun interface SetPotSettlementInfoUseCase {
    suspend operator fun invoke(
        game: Game,
        pots: List<PotSettlementDialogUiState.PotUiState>,
    )
}
