package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Phase {
    val phaseId: PhaseId

    sealed interface BetPhase : Phase {
        val actionStateList: List<BetPhaseAction>
        val phaseStatus: PhaseStatus

        fun copyWith(
            actionList: List<BetPhaseAction>
        ) : BetPhase {
            return when (this) {
                is PreFlop -> this.copy(
                    actionStateList = actionList,
                )

                is Flop -> this.copy(
                    actionStateList = actionList,
                )

                is Turn -> this.copy(
                    actionStateList = actionList,
                )

                is River -> this.copy(
                    actionStateList = actionList,
                )
            }
        }

        fun copyWith(
            phaseStatus: PhaseStatus,
        ): BetPhase {
            return when (this) {
                is PreFlop -> this.copy(
                    phaseStatus = phaseStatus,
                )

                is Flop -> this.copy(
                    phaseStatus = phaseStatus,
                )

                is Turn -> this.copy(
                    phaseStatus = phaseStatus,
                )

                is River -> this.copy(
                    phaseStatus = phaseStatus,
                )
            }
        }
    }

    data class Standby(
        override val phaseId: PhaseId,
    ) : Phase

    data class PreFlop(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
        override val phaseStatus: PhaseStatus = PhaseStatus.Active,
    ) : BetPhase

    data class Flop(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
        override val phaseStatus: PhaseStatus = PhaseStatus.Active,
    ) : BetPhase

    data class Turn(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
        override val phaseStatus: PhaseStatus = PhaseStatus.Active,
    ) : BetPhase

    data class River(
        override val phaseId: PhaseId,
        override val actionStateList: List<BetPhaseAction>,
        override val phaseStatus: PhaseStatus = PhaseStatus.Active,
    ) : BetPhase

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



