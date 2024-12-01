package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import javax.inject.Inject


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
        val isHost = table.hostPlayerId == myPlayerId
        if (!isHost) {
            // ホストじゃないとき
            when (table.tableStatus) {
                TableStatus.GAME -> {
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
                        val copiedTable = table.copy(
                            waitPlayers = waitPlayers,
                            playerOrder = addPlayerOrderIfNeed(table.playerOrder, myPlayerId),
                            updateTime = System.currentTimeMillis(),
                            version = table.version + 1
                        )
                        tableRepository.sendTable(copiedTable)
                    }
                }

                TableStatus.STANDBY, TableStatus.PAUSED -> {
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
                        val newTable = table.copy(
                            basePlayers = basePlayers,
                            playerOrder = addPlayerOrderIfNeed(table.playerOrder, myPlayerId),
                            waitPlayers = waitPlayers
                        )
                        tableRepository.sendTable(newTable)
                    }
                }
            }
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