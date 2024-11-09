package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableStateMapper
@Inject
constructor() {

    fun toMap(tableState: TableState): Map<String, Any> = hashMapOf(
        "version" to tableState.version,
        "name" to tableState.name,
        "hostPlayerId" to tableState.hostPlayerId.value,
        "btnPlayerId" to tableState.btnPlayerId.value,
        "ruleStatus" to when (val ruleState = tableState.ruleState) {
            is RuleState.LingGame -> {
                mapOf(
                    "type" to "LingGame",
                    "betViewMode" to tableState.ruleState.betViewMode.name,
                    "sbSize" to ruleState.sbSize,
                    "bbSize" to ruleState.bbSize
                )
            }
        },
        "basePlayers" to tableState.basePlayers.map {
            hashMapOf(
                "playerId" to it.id.value,
                "name" to it.name,
                "stack" to it.stack
            )
        },
        "waitPlayers" to tableState.waitPlayers.map {
            hashMapOf(
                "playerId" to it.id.value,
                "name" to it.name,
                "stack" to it.stack
            )
        },
        "playerOrder" to tableState.playerOrder.map {
            it.value
        },
        "btnPlayerId" to tableState.btnPlayerId.value,
        "startTime" to tableState.startTime
    )
}