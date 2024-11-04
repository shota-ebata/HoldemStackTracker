package com.ebata_shota.holdemstacktracker.domain.model

sealed interface ActionState {
    val actionId: Long
    val playerId: PlayerId
}

sealed interface BetPhaseActionState : ActionState {

    sealed interface BetAction : BetPhaseActionState {
        val betSize: Double
    }

    sealed interface AutoAction

    data class Blind(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Double
    ) : BetAction, AutoAction

    sealed interface PlayerAction

    data class Fold(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : BetPhaseActionState, PlayerAction

    data class Check(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : BetPhaseActionState, PlayerAction

    data class Call(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Double
    ) : BetAction, PlayerAction

    data class Bet(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Double
    ) : BetAction, PlayerAction

    data class Raise(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Double
    ) : BetAction, PlayerAction

    data class AllIn(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Double
    ) : BetAction, PlayerAction

    sealed interface Skip: AutoAction

    data class FoldSkip(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : BetPhaseActionState, Skip

    data class AllInSkip(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : BetPhaseActionState, Skip

}