package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Phase {
    val phaseId: PhaseId

    sealed interface BetPhase : Phase {
        val actionStateList: List<BetPhaseAction>
    }

    data class Standby(
        override val phaseId: PhaseId,
    ) : Phase

    data class PreFlop(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
    ) : BetPhase

    data class Flop(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
    ) : BetPhase

    data class Turn(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
    ) : BetPhase

    data class River(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
    ) : BetPhase

    data class ShowDown(
        override val phaseId: PhaseId,
    ) : Phase

    data class AllInOpen(
        override val phaseId: PhaseId,
    ) : Phase

    /**
     * ポット決済フェーズ
     */
    data class PotSettlement(
        override val phaseId: PhaseId,
    ) : Phase

    data class End(
        override val phaseId: PhaseId,
    ) : Phase

}



