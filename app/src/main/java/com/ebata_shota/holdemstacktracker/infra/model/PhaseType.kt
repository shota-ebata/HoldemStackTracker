package com.ebata_shota.holdemstacktracker.infra.model

import androidx.annotation.Keep
import com.ebata_shota.holdemstacktracker.domain.model.Phase

@Keep
enum class PhaseType {
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

        fun of(label: String): PhaseType {
            return entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }

        fun of(phase: Phase): PhaseType{
            return when (phase) {
                is Phase.Standby -> Standby
                is Phase.PreFlop -> PreFlop
                is Phase.Flop -> Flop
                is Phase.Turn -> Turn
                is Phase.River -> River
                is Phase.ShowDown -> ShowDown
                is Phase.AllInOpen -> AllInOpen
                is Phase.PotSettlement -> PotSettlement
                is Phase.End -> End
            }
        }
    }
}