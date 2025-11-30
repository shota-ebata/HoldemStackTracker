package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinPlayerFromWaitPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import javax.inject.Inject

class JoinPlayerFromWaitPlayerUseCaseImpl
@Inject
constructor(
    private val updateTable: UpdateTableUseCase,
) : JoinPlayerFromWaitPlayerUseCase {

    override suspend fun invoke(table: Table) {
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
        val newTable = table.copy(
            basePlayers = newBasePlayers,
            playerOrder = newPlayerOrder,
            // FIXME: 参加を制限する場合はすべて参加になるわけじゃなくなるので要調整
            waitPlayerIds = newWaitPlayerIds,
        )
        if (newTable != table) {
            updateTable.invoke(
                table = newTable
            )
        }
    }

    companion object {
        private const val MAX_PLAYER_SIZE = 10
    }
}