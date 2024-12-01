package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.Game
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
            players = mapToGamePlayerStateList(gameMap[PLAYERS] as List<*>),
            podStateList = mapToPodStateList(gameMap[PODS] as List<*>),
            phaseStateList = mapToPhaseStateList(gameMap[PHASES] as List<*>),
            updateTime = gameMap[UPDATE_TIME] as Long
        )
    }

    private fun mapToPhaseStateList(phases: List<*>): List<PhaseState> {
        return phases.map { it as Map<*, *> }.map {
            val phaseType = it[PHASE_TYPE] as String
            val actions = it[PHASE_ACTIONS] as? List<*>
            when (Phase.of(phaseType)) {
                Phase.Standby -> PhaseState.Standby
                Phase.PreFlop -> PhaseState.PreFlop(actionStateList = mapToActionStateList(actions!!))
                Phase.Flop -> PhaseState.Flop(actionStateList = mapToActionStateList(actions!!))
                Phase.Turn -> PhaseState.Turn(actionStateList = mapToActionStateList(actions!!))
                Phase.River ->  PhaseState.River(actionStateList = mapToActionStateList(actions!!))
                Phase.ShowDown -> PhaseState.ShowDown
                Phase.AllInOpen -> PhaseState.AllInOpen
                Phase.PotSettlement -> PhaseState.PotSettlement
                Phase.End -> PhaseState.End
            }

        }
    }

    private fun mapToActionStateList(actions: List<*>): List<BetPhaseActionState> {
        return actions.map { it as Map<*, *> }.map {
            val playerId = PlayerId(it[PHASE_ACTION_PLAYER_ID] as String)
            val actionType = it[PHASE_ACTION_ACTION_TYPE] as String
            val betSize = it[PHASE_ACTION_BET_SIZE]?.getDouble()
            when(BetPhaseAction.of(actionType)) {
                BetPhaseAction.Blind -> BetPhaseActionState.Blind(playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Fold -> BetPhaseActionState.Fold(playerId = playerId)
                BetPhaseAction.Check -> BetPhaseActionState.Check(playerId = playerId)
                BetPhaseAction.Call -> BetPhaseActionState.Call(playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Bet -> BetPhaseActionState.Bet(playerId = playerId, betSize = betSize!!)
                BetPhaseAction.Raise -> BetPhaseActionState.Raise(playerId = playerId, betSize = betSize!!)
                BetPhaseAction.AllIn -> BetPhaseActionState.AllIn(playerId = playerId, betSize = betSize!!)
                BetPhaseAction.AllInSkip -> BetPhaseActionState.AllInSkip(playerId = playerId)
                BetPhaseAction.FoldSkip -> BetPhaseActionState.FoldSkip(playerId = playerId)
            }
        }
    }

    private fun mapToGamePlayerStateList(players: List<*>): List<GamePlayerState> {
        return players.map { it as Map<*, *> }.map {
            val playerId = it[PLAYER_STATE_PLAYER_ID] as String
            val stack = it[PLAYER_STATE_PLAYER_STACK]!!.getDouble()
            val isLeaved = it[PLAYER_STATE_PLAYER_IS_LEAVED] as Boolean
            GamePlayerState(
                id = PlayerId(playerId),
                stack = stack,
                isLeaved = isLeaved
            )
        }
    }

    private fun mapToPodStateList(pods: List<*>): List<PodState> {
        return pods.map { it as Map<*, *> }.mapIndexed { index, map ->
            val podSize = map[POD_SIZE]!!.getDouble()
            val isClosed = map[POD_IS_CLOSED] as Boolean
            val involvedPlayerIds = (map[POD_INVOLVED_PLAYER_IDS] as List<*>).map {
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
        newGame: Game
    ): HashMap<String, Any> = hashMapOf(
        GAME_VERSION to newGame.version,
        APP_VERSION to newGame.appVersion,
        UPDATE_TIME to newGame.updateTime,
        PLAYERS to mapPlayers(newGame.players),
        PODS to mapPods(newGame.podStateList),
        PHASES to mapPhases(newGame.phaseStateList)
    )

    private fun mapPhases(phaseStateList: List<PhaseState>) = phaseStateList.mapIndexed { phaseIndex, phaseState ->
        phaseIndex.toString() to mapPhase(phaseState)
    }.toMap()

    private fun mapPhase(phaseState: PhaseState) = listOfNotNull(
        PHASE_TYPE to Phase.of(phaseState).name,
        if (phaseState is PhaseState.BetPhase) {
            PHASE_ACTIONS to mapActions(phaseState.actionStateList)
        } else {
            null
        }
    ).toMap()

    private fun mapActions(actionStateList: List<ActionState>) = actionStateList.mapIndexed { actionIndex, betPhaseActionState ->
        actionIndex.toString() to mapAction(betPhaseActionState)
    }.toMap()

    private fun mapAction(betPhaseActionState: ActionState) = listOfNotNull(
        PHASE_ACTION_PLAYER_ID to betPhaseActionState.playerId.value,
        PHASE_ACTION_ACTION_TYPE to BetPhaseAction.of(betPhaseActionState).name,
        if (betPhaseActionState is BetPhaseActionState.BetAction) {
            PHASE_ACTION_BET_SIZE to betPhaseActionState.betSize
        } else {
            null
        }
    ).toMap()

    private fun mapPods(podStateList: List<PodState>) = podStateList.associate { podState ->
        podState.podNumber.toString() to mapPod(podState)
    }

    private fun mapPod(podState: PodState) = hashMapOf(
        POD_SIZE to podState.podSize,
        POD_IS_CLOSED to podState.isClosed,
        POD_INVOLVED_PLAYER_IDS to mapInvolvedPlayerIds(podState)
    )

    private fun mapInvolvedPlayerIds(podState: PodState) = podState.involvedPlayerIds.mapIndexed { index, playerId ->
        index.toString() to playerId.value
    }.toMap()

    private fun mapPlayers(players: List<GamePlayerState>) = players.mapIndexed { index, gamePlayerState ->
        index.toString() to mapGamePlayer(gamePlayerState)
    }.toMap()

    private fun mapGamePlayer(gamePlayerState: GamePlayerState) = hashMapOf(
        PLAYER_STATE_PLAYER_ID to gamePlayerState.id.value,
        PLAYER_STATE_PLAYER_STACK to gamePlayerState.stack,
        PLAYER_STATE_PLAYER_IS_LEAVED to gamePlayerState.isLeaved
    )
}