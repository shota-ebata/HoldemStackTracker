package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Action {
    val actionId: ActionId
    val playerId: PlayerId
}

sealed interface BetPhaseAction : Action {

    sealed interface BetAction : BetPhaseAction {
        val betSize: Int
    }

    sealed interface AutoAction : BetPhaseAction

    data class Blind(
        override val actionId: ActionId,
        override val playerId: PlayerId,
        override val betSize: Int
    ) : BetAction, AutoAction

    sealed interface PlayerAction

    data class Fold(
        override val actionId: ActionId,
        override val playerId: PlayerId
    ) : BetPhaseAction, PlayerAction

    data class Check(
        override val actionId: ActionId,
        override val playerId: PlayerId
    ) : BetPhaseAction, PlayerAction

    data class Call(
        override val actionId: ActionId,
        override val playerId: PlayerId,
        override val betSize: Int
    ) : BetAction, PlayerAction

    data class Bet(
        override val actionId: ActionId,
        override val playerId: PlayerId,
        override val betSize: Int
    ) : BetAction, PlayerAction

    data class Raise(
        override val actionId: ActionId,
        override val playerId: PlayerId,
        override val betSize: Int
    ) : BetAction, PlayerAction

    data class AllIn(
        override val actionId: ActionId,
        override val playerId: PlayerId,
        override val betSize: Int
    ) : BetAction, PlayerAction

    sealed interface Skip: AutoAction

    data class FoldSkip(
        override val actionId: ActionId,
        override val playerId: PlayerId
    ) : BetPhaseAction, Skip

    data class AllInSkip(
        override val actionId: ActionId,
        override val playerId: PlayerId
    ) : BetPhaseAction, Skip

}