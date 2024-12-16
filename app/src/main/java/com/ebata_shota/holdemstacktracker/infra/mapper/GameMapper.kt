package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pod
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType
import com.ebata_shota.holdemstacktracker.infra.model.PhaseType
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameMapper
@Inject
constructor() {

    companion object {
        private const val GAME_VERSION = "gameVersion"
        private const val APP_VERSION = "appVersion"
        private const val PLAYERS = "players"
        private const val PLAYER_STATE_PLAYER_ID = "playerId"
        private const val PLAYER_STATE_PLAYER_STACK = "stack"
        private const val PLAYER_STATE_PLAYER_IS_LEAVED = "isLeaved"
        private const val PODS = "pods"
        private const val POD_SIZE = "podSize"
        private const val POD_IS_CLOSED = "isClosed"
        private const val POD_INVOLVED_PLAYER_IDS = "involvedPlayerIds"
        private const val PHASES = "phases"
        private const val PHASE_TYPE = "phaseType"
        private const val PHASE_ACTIONS = "actions"
        private const val PHASE_ACTION_PLAYER_ID = "playerId"
        private const val PHASE_ACTION_ACTION_TYPE = "actionType"
        private const val PHASE_ACTION_BET_SIZE = "betSize"
        private const val UPDATE_TIME = "updateTime"
    }

    fun mapToGame(gameMap: Map<*, *>): Game {

        return Game(
            version = gameMap[GAME_VERSION] as Long,
            appVersion = gameMap[APP_VERSION] as Long,
            players = mapToGamePlayerStateList(gameMap[PLAYERS] as Map<*, *>),
            podList = (gameMap[PODS] as? List<*>)?.let {
                mapToPodStateList(it)
            } ?: emptyList(),
            phaseList = mapToPhaseStateList(gameMap[PHASES] as List<*>),
            updateTime = Instant.ofEpochMilli(gameMap[UPDATE_TIME] as Long)
        )
    }

    private fun mapToPhaseStateList(phases: List<*>): List<Phase> {
        return phases.map { it as Map<*, *> }.map {
            val phaseType = it[PHASE_TYPE] as String
            val actions = it[PHASE_ACTIONS] as? List<*>
            when (PhaseType.of(phaseType)) {
                PhaseType.Standby -> Phase.Standby
                PhaseType.PreFlop -> Phase.PreFlop(actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty())
                PhaseType.Flop -> Phase.Flop(actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty())
                PhaseType.Turn -> Phase.Turn(actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty())
                PhaseType.River ->  Phase.River(actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty())
                PhaseType.ShowDown -> Phase.ShowDown
                PhaseType.AllInOpen -> Phase.AllInOpen
                PhaseType.PotSettlement -> Phase.PotSettlement
                PhaseType.End -> Phase.End
            }

        }
    }

    private fun mapToActionStateList(actions: List<*>): List<BetPhaseAction> {
        return actions.map { it as Map<*, *> }.map {
            val playerId = PlayerId(it[PHASE_ACTION_PLAYER_ID] as String)
            val actionType = it[PHASE_ACTION_ACTION_TYPE] as String
            val betSize = it[PHASE_ACTION_BET_SIZE]?.getDouble()
            when(BetPhaseActionType.of(actionType)) {
                BetPhaseActionType.Blind -> BetPhaseAction.Blind(playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Fold -> BetPhaseAction.Fold(playerId = playerId)
                BetPhaseActionType.Check -> BetPhaseAction.Check(playerId = playerId)
                BetPhaseActionType.Call -> BetPhaseAction.Call(playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Bet -> BetPhaseAction.Bet(playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Raise -> BetPhaseAction.Raise(playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.AllIn -> BetPhaseAction.AllIn(playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.AllInSkip -> BetPhaseAction.AllInSkip(playerId = playerId)
                BetPhaseActionType.FoldSkip -> BetPhaseAction.FoldSkip(playerId = playerId)
            }
        }
    }

    private fun mapToGamePlayerStateList(players: Map<*, *>): Set<GamePlayer> {
        val keys = players.keys
        return keys.map { key ->
            val value = players[key] as Map<*, *>
            val playerId = key as String
            val stack = value[PLAYER_STATE_PLAYER_STACK]!!.getDouble()
            val isLeaved = value[PLAYER_STATE_PLAYER_IS_LEAVED] as Boolean
            GamePlayer(
                id = PlayerId(playerId),
                stack = stack,
                isLeaved = isLeaved
            )
        }.toSet()
    }

    private fun mapToPodStateList(pods: List<*>): List<Pod> {
        return pods.map { it as Map<*, *> }.mapIndexed { index, map ->
            val podSize = map[POD_SIZE]!!.getDouble()
            val isClosed = map[POD_IS_CLOSED] as Boolean
            val involvedPlayerIds = (map[POD_INVOLVED_PLAYER_IDS] as List<*>).map {
                PlayerId(it as String)
            }
            Pod(
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
        newGame: Game
    ): HashMap<String, Any> = hashMapOf(
        GAME_VERSION to newGame.version,
        APP_VERSION to newGame.appVersion,
        UPDATE_TIME to newGame.updateTime.toEpochMilli(),
        PLAYERS to mapPlayers(newGame.players),
        PODS to mapPods(newGame.podList),
        PHASES to mapPhases(newGame.phaseList)
    )

    private fun mapPhases(phaseList: List<Phase>) = phaseList.mapIndexed { phaseIndex, phaseState ->
        phaseIndex.toString() to mapPhase(phaseState)
    }.toMap()

    private fun mapPhase(phase: Phase) = listOfNotNull(
        PHASE_TYPE to PhaseType.of(phase).name,
        if (phase is Phase.BetPhase) {
            PHASE_ACTIONS to mapActions(phase.actionStateList)
        } else {
            null
        }
    ).toMap()

    private fun mapActions(actionList: List<Action>) = actionList.mapIndexed { actionIndex, betPhaseActionState ->
        actionIndex.toString() to mapAction(betPhaseActionState)
    }.toMap()

    private fun mapAction(betPhaseAction: Action) = listOfNotNull(
        PHASE_ACTION_PLAYER_ID to betPhaseAction.playerId.value,
        PHASE_ACTION_ACTION_TYPE to BetPhaseActionType.of(betPhaseAction).name,
        if (betPhaseAction is BetPhaseAction.BetAction) {
            PHASE_ACTION_BET_SIZE to betPhaseAction.betSize
        } else {
            null
        }
    ).toMap()

    private fun mapPods(podList: List<Pod>) = podList.associate { podState ->
        podState.podNumber.toString() to mapPod(podState)
    }

    private fun mapPod(pod: Pod) = hashMapOf(
        POD_SIZE to pod.podSize,
        POD_IS_CLOSED to pod.isClosed,
        POD_INVOLVED_PLAYER_IDS to mapInvolvedPlayerIds(pod)
    )

    private fun mapInvolvedPlayerIds(pod: Pod) = pod.involvedPlayerIds.mapIndexed { index, playerId ->
        index.toString() to playerId.value
    }.toMap()

    private fun mapPlayers(players: Set<GamePlayer>) = players.map { gamePlayer ->
        gamePlayer.id to mapGamePlayer(gamePlayer)
    }

    private fun mapGamePlayer(gamePlayer: GamePlayer) = hashMapOf(
        PLAYER_STATE_PLAYER_STACK to gamePlayer.stack,
        PLAYER_STATE_PLAYER_IS_LEAVED to gamePlayer.isLeaved
    )
}