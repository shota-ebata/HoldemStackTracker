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
                    val newWaitPlayers = table.waitPlayers.toMutableList()
                    table.waitPlayers.forEach { waitPlayer ->
                        if (table.playerOrder.none { it == waitPlayer.id }) {
                            // waitのプレイヤーがnewPlayerOrderにいない場合
                            if (newPlayerOrder.size < MAX_PLAYER_SIZE) {
                                // 10人未満なら
                                // orderに追加
                                newPlayerOrder.add(waitPlayer.id)
                                // baseに追加（必要なら）
                                if (table.basePlayers.none { it.id == waitPlayer.id }) {
                                    newBasePlayers.add(waitPlayer)
                                }
                                // waitから削除
                                newWaitPlayers.removeIf { it.id == waitPlayer.id }
                            }
                        }
                    }
                    newTable = table.copy(
                        basePlayers = newBasePlayers,
                        playerOrder = newPlayerOrder,
                        // FIXME: 参加を制限する場合はすべて参加になるわけじゃなくなるので要調整
                        waitPlayers = newWaitPlayers,
                    )
                }
            }
        } else {
            // ホストじゃないとき
            if (
                table.basePlayers.none { it.id == myPlayerId }
                && table.waitPlayers.none { it.id == myPlayerId }
            ) {
                // baseにもwaitにも自分がいないなら
                // waitに自分を追加
                // baseへの追加はホストにやってもらう
                val waitPlayers = table.waitPlayers + PlayerBaseState(
                    id = myPlayerId,
                    name = myName,
                    stack = table.rule.defaultStack
                )
                newTable = table.copy(
                    waitPlayers = waitPlayers,
                    playerOrder = addPlayerOrderIfNeed(table.playerOrder, myPlayerId)
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