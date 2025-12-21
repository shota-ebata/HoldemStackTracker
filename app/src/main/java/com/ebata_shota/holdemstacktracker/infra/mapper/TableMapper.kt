package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.google.firebase.database.DataSnapshot
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableMapper
@Inject
constructor() {

    companion object {
        const val TABLE_VERSION = "tableVersion"
        private const val HOST_APP_VERSION_CODE = "hostAppVersionCode"
        private const val HOST_PLAYER_ID = "hostPlayerId"
        private const val POT_MANAGER_ID = "potManagerId"
        const val RULE = "rule"
        const val RULE_TYPE = "type"
        const val RULE_TYPE_RING_GAME = "RingGame"
        const val RULE_SB_SIZE = "sbSize"
        const val RULE_BB_SIZE = "bbSize"
        const val RULE_DEFAULT_STACK = "defaultStack"
        const val BASE_PLAYER_IDS = "basePlayerIds"
        const val PLAYER_NAME_INFO = "playerNameInfo"
        const val PLAYER_SEATED_INFO = "playerSeatedInfo"
        const val PLAYER_CONNECTION_INFO = "playerConnectionInfo"
        const val PLAYER_STACK_INFO = "playerStackInfo"
        const val WAIT_PLAYER_IDS = "waitPlayerIds"
        const val PLAYER_ORDER = "playerOrder"
        const val BAN_PLAYER_IDS = "banPlayerIds"
        private const val BTN_PLAYER_ID = "btnPlayerId"
        const val TABLE_STATUS = "tableStatus"
        const val CURRENT_GAME_ID = "currentGameId"
        private const val START_TIME = "startTime"
        private const val TABLE_CREATE_TIME = "tableCreateTime"
        const val UPDATE_TIME = "updateTime"
    }

    private fun Any.getInt() = (this as? Long)?.toInt()

    fun mapToTableState(
        tableId: TableId,
        tableSnapshot: DataSnapshot,
    ): Table {
        val tableMap: Map<*, *> = tableSnapshot.value as Map<*, *>
        return Table(
            id = tableId,
            version = tableMap[TABLE_VERSION] as Long,
            hostAppVersionCode = (tableMap[HOST_APP_VERSION_CODE] as? Number)?.toInt() ?: 0,
            hostPlayerId = PlayerId(tableMap[HOST_PLAYER_ID] as String),
            potManagerPlayerId = PlayerId(tableMap[POT_MANAGER_ID] as String),
            rule = mapToRuleState(tableMap[RULE] as Map<*, *>),
            basePlayers = mapToBasePlayers(
                basePlayerIds = tableSnapshot.child(BASE_PLAYER_IDS).children.map {
                    PlayerId(it.value as String)
                },
                playerNameInfoMap = tableMap[PLAYER_NAME_INFO] as Map<*, *>,
                platerSeatedInfoMap = tableMap[PLAYER_SEATED_INFO] as Map<*, *>,
                connectionInfoMap = tableMap[PLAYER_CONNECTION_INFO] as Map<*, *>,
                playerStackInfoMap = tableMap[PLAYER_STACK_INFO] as Map<*, *>,
            ),
            waitPlayerIds = tableSnapshot.child(WAIT_PLAYER_IDS).children.associate {
                it.key!! to PlayerId(it.value!! as String)
            },
            playerOrder = (tableMap[PLAYER_ORDER] as List<*>).map { PlayerId(it as String) },
            banPlayerIds = tableSnapshot.child(BAN_PLAYER_IDS).children.map {
                PlayerId(it.value as String)
            },
            btnPlayerId = PlayerId(tableMap[BTN_PLAYER_ID] as String),
            tableStatus = TableStatus.of(tableMap[TABLE_STATUS] as String),
            currentGameId = (tableMap[CURRENT_GAME_ID] as? String)?.let { GameId(it) },
            startTime = tableMap.getOrDefault(START_TIME, null)?.let {
                Instant.ofEpochMilli(it as Long)
            },
            tableCreateTime = Instant.ofEpochMilli(tableMap[TABLE_CREATE_TIME] as Long),
            updateTime = Instant.ofEpochMilli(tableMap[UPDATE_TIME] as Long)
        )
    }

    private fun mapToBasePlayers(
        basePlayerIds: List<PlayerId>,
        playerNameInfoMap: Map<*, *>,
        platerSeatedInfoMap: Map<*, *>,
        connectionInfoMap: Map<*, *>,
        playerStackInfoMap: Map<*, *>,
    ): List<PlayerBase> {
        return basePlayerIds.map { playerId ->
            PlayerBase(
                id = playerId,
                name = playerNameInfoMap[playerId.value] as String,
                stack = playerStackInfoMap[playerId.value]?.getInt() ?: 0,
                isSeated = (platerSeatedInfoMap[playerId.value] as? Boolean) ?: true,
                isConnected = (connectionInfoMap[playerId.value]as? Boolean) ?: false,
                lostConnectTimestamp = (connectionInfoMap[playerId.value] as? Long)?.let {
                    Instant.ofEpochMilli(it)
                },
            )
        }
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
        PLAYER_NAME_INFO to table.basePlayers.associate {
            it.id.value to it.name
        },
        PLAYER_STACK_INFO to table.basePlayers.associate {
            it.id.value to it.stack
        },
        PLAYER_SEATED_INFO to table.basePlayers.associate {
            it.id.value to it.isSeated
        },
        PLAYER_CONNECTION_INFO to table.basePlayers.associate {
            it.id.value to it.isConnected
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
            CURRENT_GAME_ID to it.value
        },
        table.startTime?.let {
            START_TIME to it.toEpochMilli()
        },
        TABLE_CREATE_TIME to table.tableCreateTime.toEpochMilli(),
        UPDATE_TIME to table.updateTime.toEpochMilli() // TODO: ServerValue.TIMESTAMPにしたほうがいいかも？
    ).toMap()
}