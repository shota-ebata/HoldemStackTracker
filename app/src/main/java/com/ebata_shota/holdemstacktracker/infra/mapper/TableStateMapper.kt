package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableStateMapper
@Inject
constructor() {

    private fun Any.getDouble() = (this as? Double) ?: (this as Long).toDouble()

    fun mapToTableState(tableId: TableId, tableMap: Map<*, *>): TableState {
        return TableState(
            id = tableId,
            version = tableMap["tableVersion"] as Long,
            appVersion = tableMap["appVersion"] as Long,
            name = tableMap["name"] as String,
            hostPlayerId = PlayerId(tableMap["hostPlayerId"] as String),
            ruleState = mapToRuleState(tableMap["rule"] as Map<*, *>),
            basePlayers = mapToBasePlayers(tableMap["basePlayers"] as List<*>),
            waitPlayers = (tableMap["waitPlayers"] as? List<*>)?.let {
                mapToBasePlayers(it)
            } ?: emptyList(),
            playerOrder = (tableMap["playerOrder"] as List<*>).map { PlayerId(it as String) },
            btnPlayerId = PlayerId(tableMap["btnPlayerId"] as String),
            startTime = tableMap["startTime"] as Long
        )
    }

    private fun mapToBasePlayers(basePlayers: List<*>) = basePlayers.map { it as Map<*, *> }.map {
        PlayerBaseState(
            id = PlayerId(it["playerId"] as String),
            name = it["name"] as String,
            stack = it["stack"]!!.getDouble()
        )
    }

    private fun mapToRuleState(rule: Map<*, *>) = when (val ruleType = rule["type"] as String) {
        "LingGame" -> {
            RuleState.LingGame(
                sbSize = rule["sbSize"]!!.getDouble(),
                bbSize = rule["bbSize"]!!.getDouble(),
                betViewMode = BetViewMode.of(rule["betViewMode"] as String),
                defaultStack = rule["defaultStack"]!!.getDouble()
            )
        }

        else -> throw IllegalStateException("unsupported ruleType = $ruleType")
    }

    fun toMap(tableState: TableState): Map<String, Any> = hashMapOf(
        "tableVersion" to tableState.version,
        "appVersion" to tableState.appVersion,
        "name" to tableState.name,
        "hostPlayerId" to tableState.hostPlayerId.value,
        "btnPlayerId" to tableState.btnPlayerId.value,
        "rule" to when (val ruleState = tableState.ruleState) {
            is RuleState.LingGame -> {
                mapOf(
                    "type" to "LingGame",
                    "betViewMode" to tableState.ruleState.betViewMode.name,
                    "sbSize" to ruleState.sbSize,
                    "bbSize" to ruleState.bbSize,
                    "defaultStack" to ruleState.defaultStack
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