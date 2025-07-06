package com.ebata_shota.holdemstacktracker.infra.mapper

<<<<<<< HEAD
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.exception.AppVersionException
=======
>>>>>>> main
import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableMapper
@Inject
constructor() {

    companion object {
        private const val TABLE_VERSION = "tableVersion"
        private const val HOST_APP_VERSION_CODE = "hostAppVersionCode"
        private const val HOST_PLAYER_ID = "hostPlayerId"
        private const val POT_MANAGER_ID = "potManagerId"
        private const val RULE = "rule"
        private const val RULE_TYPE = "type"
        private const val RULE_TYPE_RING_GAME = "RingGame"
        private const val RULE_SB_SIZE = "sbSize"
        private const val RULE_BB_SIZE = "bbSize"
        private const val RULE_DEFAULT_STACK = "defaultStack"
        private const val BASE_PLAYERS = "basePlayers"
        private const val WAIT_PLAYER_IDS = "waitPlayerIds"
        private const val PLAYER_ID = "playerId"
        private const val PLAYER_NAME = "name"
        private const val PLAYER_STACK = "stack"
        private const val PLAYER_IS_LEAVED = "isLeaved"
        private const val PLAYER_ORDER = "playerOrder"
        private const val BTN_PLAYER_ID = "btnPlayerId"
        private const val TABLE_STATUS = "tableStatus"
        private const val CURRENT_TABLE_ID = "currentTableId"
        private const val START_TIME = "startTime"
        private const val TABLE_CREATE_TIME = "tableCreateTime"
        private const val UPDATE_TIME = "updateTime"
    }

    private fun Any.getInt() = (this as? Long)?.toInt()

    @Throws(AppVersionException::class)
    fun mapToTableState(tableId: TableId, tableMap: Map<*, *>): Table {
        val appVersion = tableMap[APP_VERSION]?.getInt() ?: 0
        if (appVersion > BuildConfig.VERSION_CODE) {
            throw AppVersionException()
        }
        return Table(
            id = tableId,
            version = tableMap[TABLE_VERSION] as Long,
<<<<<<< HEAD
            appVersion = appVersion,
=======
            hostAppVersionCode = (tableMap[HOST_APP_VERSION_CODE] as? Number)?.toInt() ?: 0,
>>>>>>> main
            hostPlayerId = PlayerId(tableMap[HOST_PLAYER_ID] as String),
            potManagerPlayerId = PlayerId(tableMap[POT_MANAGER_ID] as String),
            rule = mapToRuleState(tableMap[RULE] as Map<*, *>),
            basePlayers = mapToBasePlayers(tableMap[BASE_PLAYERS] as List<*>),
            waitPlayerIds = (tableMap[WAIT_PLAYER_IDS] as? List<*>)?.map { PlayerId(it as String) } ?: emptyList(),
            playerOrder = (tableMap[PLAYER_ORDER] as List<*>).map { PlayerId(it as String) },
            btnPlayerId = PlayerId(tableMap[BTN_PLAYER_ID] as String),
            tableStatus = TableStatus.of(tableMap[TABLE_STATUS] as String),
            currentGameId = (tableMap[CURRENT_TABLE_ID] as? String)?.let { GameId(it) },
            startTime = tableMap.getOrDefault(START_TIME, null)?.let {
                Instant.ofEpochMilli(it as Long)
            },
            tableCreateTime = Instant.ofEpochMilli(tableMap[TABLE_CREATE_TIME] as Long),
            updateTime = Instant.ofEpochMilli(tableMap[UPDATE_TIME] as Long)
        )
    }

    private fun mapToBasePlayers(basePlayers: List<*>) = basePlayers.map { it as Map<*, *> }.map {
        PlayerBase(
            id = PlayerId(it[PLAYER_ID] as String),
            name = it[PLAYER_NAME] as String,
            stack = it[PLAYER_STACK]?.getInt() ?: 0,
            isLeaved = (it[PLAYER_IS_LEAVED] as? Boolean) ?: false
        )
    }

    private fun mapToRuleState(rule: Map<*, *>) = when (val ruleType = rule[RULE_TYPE] as String) {
        RULE_TYPE_RING_GAME -> {
            Rule.RingGame(
                sbSize = rule[RULE_SB_SIZE]!!.getInt()!!,
                bbSize = rule[RULE_BB_SIZE]!!.getInt()!!,
                defaultStack = rule[RULE_DEFAULT_STACK]!!.getInt()!!
            )
        }

        else -> throw IllegalStateException("unsupported ruleType = $ruleType")
    }

    fun toMap(table: Table): Map<String, Any> = listOfNotNull(
        TABLE_VERSION to table.version,
        HOST_APP_VERSION_CODE to table.hostAppVersionCode,
        HOST_PLAYER_ID to table.hostPlayerId.value,
        POT_MANAGER_ID to table.potManagerPlayerId.value,
        BTN_PLAYER_ID to table.btnPlayerId.value,
        RULE to when (val ruleState = table.rule) {
            is Rule.RingGame -> {
                mapOf(
                    RULE_TYPE to RULE_TYPE_RING_GAME,
                    RULE_SB_SIZE to ruleState.sbSize,
                    RULE_BB_SIZE to ruleState.bbSize,
                    RULE_DEFAULT_STACK to ruleState.defaultStack
                )
            }
        },
        BASE_PLAYERS to table.basePlayers.map {
            hashMapOf(
                PLAYER_ID to it.id.value,
                PLAYER_NAME to it.name,
                PLAYER_STACK to it.stack,
                PLAYER_IS_LEAVED to it.isLeaved,
            )
        },
        WAIT_PLAYER_IDS to table.waitPlayerIds.map {
            it.value
        },
        PLAYER_ORDER to table.playerOrder.map {
            it.value
        },
        BTN_PLAYER_ID to table.btnPlayerId.value,
        TABLE_STATUS to table.tableStatus.name,
        table.currentGameId?.let {
            CURRENT_TABLE_ID to it.value
        },
        table.startTime?.let {
            START_TIME to it.toEpochMilli()
        },
        TABLE_CREATE_TIME to table.tableCreateTime.toEpochMilli(),
        UPDATE_TIME to table.updateTime.toEpochMilli()
    ).toMap()
}