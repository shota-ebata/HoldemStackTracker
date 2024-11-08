package com.ebata_shota.holdemstacktracker.infra.model

import androidx.annotation.Keep
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState

@Keep
enum class Phase {
    Standby,
    PreFlop,
    Flop,
    Turn,
    River,
    ShowDown,
    AllInOpen,
    PotSettlement,
    End;

    companion object {

        fun of(label: String): Phase {
            return entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }

        fun of(phaseState: PhaseState): Phase{
            return when (phaseState) {
                is PhaseState.Standby -> Standby
                is PhaseState.PreFlop -> PreFlop
                is PhaseState.Flop -> Flop
                is PhaseState.Turn -> Turn
                is PhaseState.River -> River
                is PhaseState.ShowDown -> ShowDown
                is PhaseState.AllInOpen -> AllInOpen
                is PhaseState.PotSettlement -> PotSettlement
                is PhaseState.End -> End
            }
        }
    }
}