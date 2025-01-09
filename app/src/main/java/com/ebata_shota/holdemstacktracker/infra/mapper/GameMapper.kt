package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
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
        private const val POTS = "pots"
        private const val POT_SIZE = "potSize"
        private const val POT_IS_CLOSED = "isClosed"
        private const val POT_INVOLVED_PLAYER_IDS = "involvedPlayerIds"
        private const val PHASES = "phases"
        private const val PHASES_ID = "phasesId"
        private const val PHASE_TYPE = "phaseType"
        private const val PHASE_ACTIONS = "actions"
        private const val PHASE_ACTION_ID = "actionId"
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
            potList = (gameMap[POTS] as? List<*>)?.let {
                mapToPotStateList(it)
            } ?: emptyList(),
            phaseList = mapToPhaseStateList(gameMap[PHASES] as List<*>),
            updateTime = Instant.ofEpochMilli(gameMap[UPDATE_TIME] as Long)
        )
    }

    private fun mapToPhaseStateList(phases: List<*>): List<Phase> {
        return phases.map { it as Map<*, *> }.map {
            val phaseType = it[PHASE_TYPE] as String
            val phaseId = it[PHASES_ID] as String
            val actions = it[PHASE_ACTIONS] as? List<*>
            when (PhaseType.of(phaseType)) {
                PhaseType.Standby -> Phase.Standby(phaseId = PhaseId(phaseId))
                PhaseType.PreFlop -> Phase.PreFlop(
                    phaseId = PhaseId(phaseId),
                    actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty()
                )
                PhaseType.AfterPreFlop -> Phase.AfterPreFlop(phaseId = PhaseId(phaseId))
                PhaseType.Flop -> Phase.Flop(
                    phaseId = PhaseId(phaseId),
                    actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty()
                )
                PhaseType.AfterFlop -> Phase.AfterFlop(phaseId = PhaseId(phaseId))
                PhaseType.Turn -> Phase.Turn(
                    phaseId = PhaseId(phaseId),
                    actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty()
                )
                PhaseType.AfterTurn -> Phase.AfterTurn(phaseId = PhaseId(phaseId))
                PhaseType.River ->  Phase.River(
                    phaseId = PhaseId(phaseId),
                    actionStateList = actions?.let { mapToActionStateList(actions) }.orEmpty()
                )
                PhaseType.ShowDown -> Phase.ShowDown(phaseId = PhaseId(phaseId))
                PhaseType.AllInOpen -> Phase.AllInOpen(phaseId = PhaseId(phaseId))
                PhaseType.PotSettlement -> Phase.PotSettlement(phaseId = PhaseId(phaseId))
                PhaseType.End -> Phase.End(phaseId = PhaseId(phaseId))
            }

        }
    }

    private fun mapToActionStateList(actions: List<*>): List<BetPhaseAction> {
        return actions.map { it as Map<*, *> }.map {
            val actionId = ActionId(it[PHASE_ACTION_ID] as String)
            val playerId = PlayerId(it[PHASE_ACTION_PLAYER_ID] as String)
            val actionType = it[PHASE_ACTION_ACTION_TYPE] as String
            val betSize = it[PHASE_ACTION_BET_SIZE]?.getInt()
            when(BetPhaseActionType.of(actionType)) {
                BetPhaseActionType.Blind -> BetPhaseAction.Blind(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Fold -> BetPhaseAction.Fold(actionId = actionId, playerId = playerId)
                BetPhaseActionType.Check -> BetPhaseAction.Check(actionId = actionId, playerId = playerId)
                BetPhaseActionType.Call -> BetPhaseAction.Call(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Bet -> BetPhaseAction.Bet(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.Raise -> BetPhaseAction.Raise(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.AllIn -> BetPhaseAction.AllIn(actionId = actionId, playerId = playerId, betSize = betSize!!)
                BetPhaseActionType.AllInSkip -> BetPhaseAction.AllInSkip(actionId = actionId, playerId = playerId)
                BetPhaseActionType.FoldSkip -> BetPhaseAction.FoldSkip(actionId = actionId, playerId = playerId)
            }
        }
    }

    private fun mapToGamePlayerStateList(players: Map<*, *>): Set<GamePlayer> {
        val keys = players.keys
        return keys.map { key ->
            val value = players[key] as Map<*, *>
            val playerId = key as String
            val stack = value[PLAYER_STATE_PLAYER_STACK]!!.getInt()!!
            val isLeaved = value[PLAYER_STATE_PLAYER_IS_LEAVED] as Boolean
            GamePlayer(
                id = PlayerId(playerId),
                stack = stack,
                isLeaved = isLeaved
            )
        }.toSet()
    }

    private fun mapToPotStateList(pots: List<*>): List<Pot> {
        return pots.map { it as Map<*, *> }.mapIndexed { index, map ->
            val potSize = map[POT_SIZE]!!.getInt()!!
            val isClosed = map[POT_IS_CLOSED] as Boolean
            val involvedPlayerIds = (map[POT_INVOLVED_PLAYER_IDS] as List<*>).map {
                PlayerId(it as String)
            }
            Pot(
                id = index.toLong(),
                potNumber = 0,
                involvedPlayerIds = involvedPlayerIds,
                potSize = potSize,
                isClosed = isClosed
            )
        }
    }

    private fun Any.getInt() = (this as? Long)?.toInt()

    fun mapToHashMap(
        newGame: Game
    ): HashMap<String, Any> = hashMapOf(
        GAME_VERSION to newGame.version,
        APP_VERSION to newGame.appVersion,
        UPDATE_TIME to newGame.updateTime.toEpochMilli(),
        PLAYERS to mapPlayers(newGame.players),
        POTS to mapPots(newGame.potList),
        PHASES to mapPhases(newGame.phaseList)
    )

    private fun mapPhases(phaseList: List<Phase>) = phaseList.mapIndexed { phaseIndex, phaseState ->
        phaseIndex.toString() to mapPhase(phaseState)
    }.toMap()

    private fun mapPhase(phase: Phase) = listOfNotNull(
        PHASE_TYPE to PhaseType.of(phase).name,
        PHASES_ID to phase.phaseId.value,
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
        PHASE_ACTION_ID to betPhaseAction.actionId.value,
        PHASE_ACTION_PLAYER_ID to betPhaseAction.playerId.value,
        PHASE_ACTION_ACTION_TYPE to BetPhaseActionType.of(betPhaseAction).name,
        if (betPhaseAction is BetPhaseAction.BetAction) {
            PHASE_ACTION_BET_SIZE to betPhaseAction.betSize
        } else {
            null
        }
    ).toMap()

    private fun mapPots(potList: List<Pot>) = potList.associate { pot ->
        pot.potNumber.toString() to mapPot(pot)
    }

    private fun mapPot(pot: Pot) = hashMapOf(
        POT_SIZE to pot.potSize,
        POT_IS_CLOSED to pot.isClosed,
        POT_INVOLVED_PLAYER_IDS to mapInvolvedPlayerIds(pot)
    )

    private fun mapInvolvedPlayerIds(pot: Pot) = pot.involvedPlayerIds.mapIndexed { index, playerId ->
        index.toString() to playerId.value
    }.toMap()

    private fun mapPlayers(players: Set<GamePlayer>) = players.associate { gamePlayer ->
        gamePlayer.id.value to mapGamePlayer(gamePlayer)
    }

    private fun mapGamePlayer(gamePlayer: GamePlayer) = hashMapOf(
        PLAYER_STATE_PLAYER_STACK to gamePlayer.stack,
        PLAYER_STATE_PLAYER_IS_LEAVED to gamePlayer.isLeaved
    )
}