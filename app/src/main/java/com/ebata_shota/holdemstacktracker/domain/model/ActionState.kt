package com.ebata_shota.holdemstacktracker.domain.model

sealed interface ActionState {
    val actionId: Long
    val playerId: PlayerId

    sealed interface BetAction : ActionState {
        val betSize: Float
    }

    data class Blind(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Float
    ) : BetAction

    data class Check(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : ActionState

    data class Call(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Float
    ) : BetAction

    data class Bet(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Float
    ) : BetAction

    data class Raise(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Float
    ) : BetAction

    data class AllIn(
        override val actionId: Long,
        override val playerId: PlayerId,
        override val betSize: Float
    ) : BetAction

    data class Skip(
        override val actionId: Long,
        override val playerId: PlayerId
    ) : ActionState
}