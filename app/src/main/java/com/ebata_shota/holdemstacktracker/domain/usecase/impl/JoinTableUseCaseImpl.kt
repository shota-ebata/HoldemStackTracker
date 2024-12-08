package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
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
        var isUpdated = false
        val isHost = table.hostPlayerId == myPlayerId
        if (!isHost) { // FIXME: ホスト以外という縛りはいらない気もする（ホストが変更される可能性があるならなおさら）
            // ホストじゃないとき
            when (table.tableStatus) {
                TableStatus.PLAYING -> {
                    // ゲーム中のとき
                    if (
                        table.basePlayers.none { it.id == myPlayerId }
                        && table.waitPlayers.none { it.id == myPlayerId }
                    ) {
                        // baseにもwaitにも自分がいないなら
                        // waitに自分を追加
                        val waitPlayers = table.waitPlayers + PlayerBaseState(
                            id = myPlayerId,
                            name = myName,
                            stack = table.ruleState.defaultStack
                        )
                        newTable = table.copy(
                            waitPlayers = waitPlayers,
                            playerOrder = addPlayerOrderIfNeed(table.playerOrder, myPlayerId)
                        )
                        isUpdated = true
                    }
                }

                TableStatus.PREPARING, TableStatus.PAUSED -> {
                    // ゲーム中以外のとき
                    if (table.basePlayers.none { it.id == myPlayerId }) {
                        // baseに自分がいないなら
                        // waitから自分を消して
                        val waitPlayers = table.waitPlayers.filterNot {
                            table.basePlayers.none { it.id == myPlayerId }
                        }
                        // baseに自分を追加
                        val basePlayers = table.basePlayers + PlayerBaseState(
                            id = myPlayerId,
                            name = myName,
                            stack = table.ruleState.defaultStack
                        )
                        newTable = table.copy(
                            basePlayers = basePlayers,
                            playerOrder = addPlayerOrderIfNeed(table.playerOrder, myPlayerId),
                            waitPlayers = waitPlayers,
                        )
                        isUpdated = true
                    }
                }
            }
        }
        val index = table.basePlayers.indexOfFirstOrNull { it.id == myPlayerId }
        if (index != null) {
            val myPlayer = table.basePlayers[index]
            // プレイヤー名が異なるときの処理
            if (myPlayer.name != myName) {
                // プレイヤ名が異なるなら更新する
                newTable = newTable.copy(
                    basePlayers = newTable.basePlayers.mapAtIndex(index) {
                        it.copy(name = myName)
                    }
                )
                isUpdated = true
            }
        }
        if (isUpdated) {
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
}