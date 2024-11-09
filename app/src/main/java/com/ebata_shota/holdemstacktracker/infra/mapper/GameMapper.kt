package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.infra.model.Phase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameMapper
@Inject
constructor() {
    fun mapToGameState(gameMap: Map<*, *>): GameState {
        val version = gameMap["version"] as Long
        val timestamp = gameMap["timestamp"] as Long
        val players = gameMap["players"] as List<*>
        val pods = gameMap["pods"] as List<*>
        val phases = gameMap["phases"] as List<*>

        return GameState(
            version = version,
            players = mapToGamePlayerStateList(players),
            podStateList = mapToPodStateList(pods),
            phaseStateList = mapToPhaseStateList(phases),
            timestamp = timestamp
        )
    }

    private fun mapToPhaseStateList(phases: List<*>): List<PhaseState> {
        return phases.map { it as Map<*, *> }.mapIndexed { index, map ->
            val phaseType = map["phaseType"] as String
            val actions = map["actions"] as? List<*>
            val phaseId = index.toLong()
            when (Phase.of(phaseType)) {
                Phase.Standby -> PhaseState.Standby(phaseId = phaseId)
                Phase.PreFlop -> PhaseState.PreFlop(phaseId = phaseId, actionStateList = mapToActionStateList(actions!!))
                Phase.Flop -> PhaseState.Flop(phaseId = phaseId, actionStateList = mapToActionStateList(actions!!))
                Phase.Turn -> PhaseState.Turn(phaseId = phaseId, actionStateList = mapToActionStateList(actions!!))
                Phase.River ->  PhaseState.River(phaseId = phaseId, actionStateList = mapToActionStateList(actions!!))
                Phase.ShowDown -> PhaseState.ShowDown(phaseId = phaseId)
                Phase.AllInOpen -> PhaseState.AllInOpen(phaseId = phaseId)
                Phase.PotSettlement -> PhaseState.PotSettlement(phaseId = phaseId)
                Phase.End -> PhaseState.End(phaseId = phaseId)
            }

        }
    }

    private fun mapToActionStateList(actions: List<*>): List<BetPhaseActionState> {
        return actions.map { it as Map<*, *> }.mapIndexed { index, map ->
            val playerId = PlayerId(map["playerId"] as String)
            val actionType = map["actionType"] as String
            val betSize = map["betSize"]?.getDouble()
            val actionId = index.toLong()
            when(BetPhaseAction.of(actionType)) {
                BetPhaseAction.Blind -> BetPhaseActionState.Blind(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Fold -> BetPhaseActionState.Fold(actionId = actionId, playerId = playerId)
                BetPhaseAction.Check -> BetPhaseActionState.Check(actionId = actionId, playerId = playerId)
                BetPhaseAction.Call -> BetPhaseActionState.Call(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Bet -> BetPhaseActionState.Bet(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Raise -> BetPhaseActionState.Raise(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseAction.AllIn -> BetPhaseActionState.AllIn(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseAction.AllInSkip -> BetPhaseActionState.AllInSkip(actionId = actionId, playerId = playerId)
                BetPhaseAction.FoldSkip -> BetPhaseActionState.FoldSkip(actionId = actionId, playerId = playerId)
            }
        }
    }

    private fun mapToGamePlayerStateList(players: List<*>): List<GamePlayerState> {
        return players.map { it as Map<*, *> }.map {
            val playerId = it["playerId"] as String
            val stack = it["stack"]!!.getDouble()
            val isLeaved = it["isLeaved"] as Boolean
            GamePlayerState(
                id = PlayerId(playerId),
                stack = stack,
                isLeaved = isLeaved
            )
        }
    }

    private fun mapToPodStateList(pods: List<*>): List<PodState> {
        return pods.map { it as Map<*, *> }.mapIndexed { index, map ->
            val podSize = map["podSize"]!!.getDouble()
            val isClosed = map["isClosed"] as Boolean
            val involvedPlayerIds = (map["involvedPlayerIds"] as List<*>).map {
                PlayerId(it as String)
            }
            PodState(
                id = index.toLong(),
                podNumber = 0,
                involvedPlayerIds = involvedPlayerIds,
                podSize = podSize,
                isClosed = isClosed
            )
        }
    }

    private fun Any.getDouble() = (this as? Double) ?: (this as Long).toDouble()

    fun mapToHashMap(
        newGameState: GameState
    ): HashMap<String, Any> = hashMapOf(
        "version" to newGameState.version,
        "timestamp" to newGameState.timestamp,
        "players" to mapPlayers(newGameState.players),
        "pods" to mapPods(newGameState.podStateList),
        "phases" to mapPhases(newGameState.phaseStateList)
    )

    private fun mapPhases(phaseStateList: List<PhaseState>) = phaseStateList.mapIndexed { phaseIndex, phaseState ->
        phaseIndex.toString() to mapPhase(phaseState)
    }.toMap()

    private fun mapPhase(phaseState: PhaseState) = listOfNotNull(
        "phaseType" to Phase.of(phaseState).name,
        if (phaseState is PhaseState.BetPhase) {
            "actions" to mapActions(phaseState.actionStateList)
        } else {
            null
        }
    ).toMap()

    private fun mapActions(actionStateList: List<ActionState>) = actionStateList.mapIndexed { actionIndex, betPhaseActionState ->
        actionIndex.toString() to mapAction(betPhaseActionState)
    }.toMap()

    private fun mapAction(betPhaseActionState: ActionState) = listOfNotNull(
        "playerId" to betPhaseActionState.playerId.value,
        "actionType" to BetPhaseAction.of(betPhaseActionState).name,
        if (betPhaseActionState is BetPhaseActionState.BetAction) {
            "betSize" to betPhaseActionState.betSize
        } else {
            null
        }
    ).toMap()

    private fun mapPods(podStateList: List<PodState>) = podStateList.associate { podState ->
        podState.podNumber.toString() to mapPod(podState)
    }

    private fun mapPod(podState: PodState) = hashMapOf(
        "podSize" to podState.podSize,
        "isClosed" to podState.isClosed,
        "involvedPlayerIds" to mapInvolvedPlayerIds(podState)
    )

    private fun mapInvolvedPlayerIds(podState: PodState) = podState.involvedPlayerIds.mapIndexed { index, playerId ->
        index.toString() to playerId.value
    }.toMap()

    private fun mapPlayers(players: List<GamePlayerState>) = players.mapIndexed { index, gamePlayerState ->
        index.toString() to mapGamePlayer(gamePlayerState)
    }.toMap()

    private fun mapGamePlayer(gamePlayerState: GamePlayerState) = hashMapOf(
        "playerId" to gamePlayerState.id.value,
        "stack" to gamePlayerState.stack,
        "isLeaved" to gamePlayerState.isLeaved
    )
}