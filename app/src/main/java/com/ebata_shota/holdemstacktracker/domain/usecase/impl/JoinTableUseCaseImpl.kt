package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import java.time.Instant
import javax.inject.Inject


// FIXME: プレイヤー名が異なるときの処理 が入るので名前変えたほうがいいかも？
class JoinTableUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository
) : JoinTableUseCase {
    override suspend fun invoke(
        table: Table,
        myPlayerId: PlayerId,
        myName: String
    ) {
        var newTable: Table = table
        val isHost = table.hostPlayerId == myPlayerId
        if (isHost) {
            // ホストのときに
            when (table.tableStatus) {
                TableStatus.PLAYING -> {
                    // ゲーム中のときは何もしない？
                }

                TableStatus.PREPARING,
                TableStatus.PAUSED -> { // FIXME: PAUSEDでは table.rule is Rule.RingGame じゃないとまずいか？
                    // ゲーム中以外のとき
                    val newBasePlayers = table.basePlayers.toMutableList()
                    val newPlayerOrder = table.playerOrder.toMutableList()
                    val newWaitPlayerIds = table.waitPlayerIds.toMutableList()
                    table.waitPlayerIds.forEach { waitPlayerId ->
                        if (table.playerOrder.none { it == waitPlayerId }) {
                            // waitのプレイヤーがplayerOrderにいない場合
                            if (newPlayerOrder.size < MAX_PLAYER_SIZE) {
                                // 10人未満なら
                                // orderに追加
                                newPlayerOrder.add(waitPlayerId)
                                // waitから削除
                                newWaitPlayerIds.removeIf { it == waitPlayerId }
                            }
                        }
                    }
                    newTable = table.copy(
                        basePlayers = newBasePlayers,
                        playerOrder = newPlayerOrder,
                        // FIXME: 参加を制限する場合はすべて参加になるわけじゃなくなるので要調整
                        waitPlayerIds = newWaitPlayerIds,
                    )
                }
            }
        } else {
            // ホストじゃないとき
            if (
                table.playerOrder.none { it == myPlayerId }
                && table.waitPlayerIds.none { it == myPlayerId }
            ) {
                // playerOrderにもwaitにも自分がいないなら
                // waitに自分を追加
                // playerOrderへの追加はホストにやってもらう
                val newWaitPlayerIds = table.waitPlayerIds + myPlayerId
                var newBasePlayers = table.basePlayers
                if (table.basePlayers.none { it.id == myPlayerId }) {
                    // basePlayersに自分がいないなら追加
                    newBasePlayers = table.basePlayers + PlayerBaseState(
                        id = myPlayerId,
                        name = myName,
                        stack = table.rule.defaultStack
                    )
                }
                newTable = table.copy(
                    basePlayers = newBasePlayers,
                    waitPlayerIds = newWaitPlayerIds
                )
            }
        }

        if (newTable != table) {
            val updateTime = Instant.now()
            newTable = newTable.copy(
                updateTime = updateTime,
                version = table.version + 1
            )
            // FIXME: updateTimeとかversionの更新処理を別のUseCaseに移動させたほうが冗長解消できそう
            tableRepository.sendTable(newTable)
        }
    }

    private fun addPlayerOrderIfNeed(
        playerOrder: List<PlayerId>,
        myPlayerId: PlayerId
    ): List<PlayerId> = if (playerOrder.none { it == myPlayerId }) {
        playerOrder + myPlayerId
    } else {
        playerOrder
    }

    companion object {
        private const val MAX_PLAYER_SIZE = 10
    }
}