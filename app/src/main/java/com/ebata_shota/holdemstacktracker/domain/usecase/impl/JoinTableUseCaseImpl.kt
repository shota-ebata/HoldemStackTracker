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
        tableState: Table,
        myPlayerId: PlayerId,
        myName: String
    ) {
        val isHost = tableState.hostPlayerId == myPlayerId
        if (!isHost) {
            // ホストじゃないとき
            when (tableState.tableStatus) {
                TableStatus.GAME -> {
                    // ゲーム中のとき
                    if (
                        tableState.basePlayers.none { it.id == myPlayerId }
                        && tableState.waitPlayers.none { it.id == myPlayerId }
                    ) {
                        // baseにもwaitにも自分がいないなら
                        // waitに自分を追加
                        val waitPlayers = tableState.waitPlayers + PlayerBaseState(
                            id = myPlayerId,
                            name = myName,
                            stack = tableState.ruleState.defaultStack
                        )
                        val newTable = tableState.copy(
                            waitPlayers = waitPlayers,
                            playerOrder = addPlayerOrderIfNeed(tableState.playerOrder, myPlayerId)
                        )
                        tableRepository.sendTable(newTable)
                    }
                }

                TableStatus.STANDBY, TableStatus.PAUSED -> {
                    // ゲーム中以外のとき
                    if (tableState.basePlayers.none { it.id == myPlayerId }) {
                        // baseに自分がいないなら
                        // waitから自分を消して
                        val waitPlayers = tableState.waitPlayers.filterNot {
                            tableState.basePlayers.none { it.id == myPlayerId }
                        }
                        // baseに自分を追加
                        val basePlayers = tableState.basePlayers + PlayerBaseState(
                            id = myPlayerId,
                            name = myName,
                            stack = tableState.ruleState.defaultStack
                        )
                        val newTable = tableState.copy(
                            basePlayers = basePlayers,
                            playerOrder = addPlayerOrderIfNeed(tableState.playerOrder, myPlayerId),
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