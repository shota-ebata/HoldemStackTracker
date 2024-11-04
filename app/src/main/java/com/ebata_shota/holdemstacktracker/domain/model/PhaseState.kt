package com.ebata_shota.holdemstacktracker.domain.model

sealed interface PhaseState {
    val phaseId: Long

    sealed interface BetPhase : PhaseState {
        val actionStateList: List<BetPhaseActionState>
    }

    data class Standby(
        override val phaseId: Long
    ) : PhaseState

    data class PreFlop(
        override val phaseId: Long,
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class Flop(
        override val phaseId: Long,
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class Turn(
        override val phaseId: Long,
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class River(
        override val phaseId: Long,
        override val actionStateList: List<BetPhaseActionState>
    ) : BetPhase

    data class ShowDown(
        override val phaseId: Long
    ) : PhaseState

    data class AllInOpen(
        override val phaseId: Long
    ) : PhaseState

    /**
     * ポッド決済フェーズ
     */
    data class PotSettlement(
        override val phaseId: Long
    ) : PhaseState

    data class End(
        override val phaseId: Long
    ) : PhaseState

}



