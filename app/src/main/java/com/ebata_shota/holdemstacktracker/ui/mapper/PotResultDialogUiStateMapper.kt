package com.ebata_shota.holdemstacktracker.ui.mapper

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotResultDialogUiState
import com.ebata_shota.holdemstacktracker.ui.compose.row.PotResultRowUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class PotResultDialogUiStateMapper
@Inject
constructor() {
    fun createUiState(
        lastPhase: Phase.End,
        table: Table,
    ): PotResultDialogUiState = PotResultDialogUiState(
        potResults = lastPhase.gameResult.potResults.map { potResult ->
            PotResultRowUiState(
                potLabelText = when (potResult.potNumber) {
                    0 -> {
                        StringSource(
                            if (lastPhase.gameResult.potResults.size == 1) {
                                R.string.label_pot
                            } else {
                                R.string.label_main_pot
                            }
                        )
                    }

                    else -> {
                        StringSource(
                            R.string.label_side_pot,
                            potResult.potNumber
                        )
                    }
                },
                potSizeText = StringSource(potResult.potSize.toString()),
                winnerPlayerNames = potResult.winnerPlayerIds.map { playerId ->
                    StringSource(table.getPlayerName(playerId)!!) // FIXME: 雑かも例外発生する可能性あり
                }
            )
        }
    )
}