package com.ebata_shota.holdemstacktracker.ui.usecase

import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseStatus
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PhaseIntervalImageDialogUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class MapPhaseIntervalImageDialogUiStateUseCase
@Inject
constructor() {
    fun invoke(game: Game): PhaseIntervalImageDialogUiState? {
        return when (val lastPhase = game.phaseList.lastOrNull()) {
            is Phase.Standby -> null
            is Phase.PreFlop -> {
                when (lastPhase.phaseStatus) {
                    PhaseStatus.Close -> {
                        PhaseIntervalImageDialogUiState(
                            imageResId = R.drawable.flopimage
                        )
                    }

                    PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                        imageResId = R.drawable.all_in_show_down
                    )

                    PhaseStatus.Active -> null
                }
            }

            is Phase.Flop -> {
                when (lastPhase.phaseStatus) {
                    PhaseStatus.Close -> {
                        PhaseIntervalImageDialogUiState(
                            imageResId = R.drawable.turnimage
                        )
                    }

                    PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                        imageResId = R.drawable.all_in_show_down
                    )

                    PhaseStatus.Active -> null
                }
            }

            is Phase.Turn -> {
                when (lastPhase.phaseStatus) {
                    PhaseStatus.Close -> {
                        PhaseIntervalImageDialogUiState(
                            imageResId = R.drawable.riverimage
                        )
                    }

                    PhaseStatus.AllInClose -> PhaseIntervalImageDialogUiState(
                        imageResId = R.drawable.all_in_show_down
                    )

                    PhaseStatus.Active -> null
                }
            }

            is Phase.River -> {
                when (lastPhase.phaseStatus) {
                    PhaseStatus.Close -> {
                        PhaseIntervalImageDialogUiState(
                            imageResId = R.drawable.showdownimage
                        )
                    }

                    PhaseStatus.Active,
                    PhaseStatus.AllInClose,
                        -> null
                }
            }

            is Phase.PotSettlement -> null
            is Phase.End -> null
            null -> null
        }
    }
}