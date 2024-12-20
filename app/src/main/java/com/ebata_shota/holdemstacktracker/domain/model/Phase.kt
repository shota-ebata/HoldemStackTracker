package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Phase {

    sealed interface BetPhase : Phase {
        val actionStateList: List<BetPhaseAction>
    }

    data object Standby : Phase

    data class PreFlop(
        override val actionStateList: List<BetPhaseAction>
    ) : BetPhase

    data class Flop(
        override val actionStateList: List<BetPhaseAction>
    ) : BetPhase

    data class Turn(
        override val actionStateList: List<BetPhaseAction>
    ) : BetPhase

    data class River(
        override val actionStateList: List<BetPhaseAction>
    ) : BetPhase

    data object ShowDown : Phase

    data object AllInOpen : Phase

    /**
     * ポッド決済フェーズ
     */
    data object PotSettlement : Phase

    data object End : Phase

}



