package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.infra.model.Phase
import java.util.HashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameMapper
@Inject
constructor() {
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