package com.ebata_shota.holdemstacktracker.domain.model

sealed interface PhaseState {

    sealed interface BetPhase : PhaseState {
        val actionStateList: List<BetPhaseActionState>
    }

    data object Standby : PhaseState

    data class PreFlop(
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class Flop(
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class Turn(
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class River(
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data object ShowDown : PhaseState

    data object AllInOpen : PhaseState

    /**
     * ポッド決済フェーズ
     */
    data object PotSettlement : PhaseState

    data object End : PhaseState

}



