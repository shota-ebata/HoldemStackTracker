package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinPlayerFromWaitPlayerUseCase
import javax.inject.Inject

class JoinPlayerFromWaitPlayerUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : JoinPlayerFromWaitPlayerUseCase {

    override suspend fun invoke(table: Table) {

        val newPlayerOrder = table.playerOrder.toMutableList()
        if (table.waitPlayerIds.isEmpty() || newPlayerOrder.size >= MAX_PLAYER_SIZE) {
            // 待機中だったり、これ以上追加できないなら何もしない
            return
        }

        val addPlayerIds = mutableMapOf<String, PlayerId>()

        table.waitPlayerIds.forEach { (key, waitPlayerId) ->
            if (table.playerOrder.none { it == waitPlayerId }) {
                // waitのプレイヤーがplayerOrderにいない場合
                if (newPlayerOrder.size < MAX_PLAYER_SIZE) {
                    // 10人未満なら
                    // orderに追加
                    newPlayerOrder.add(waitPlayerId)
                    addPlayerIds[key] = waitPlayerId
                }
            }
        }
        if (addPlayerIds.isNotEmpty()) {
            tableRepository.addPlayerOrder(
                tableId = table.id,
                newPlayerOrder = newPlayerOrder,
                addPlayerIds = addPlayerIds,
            )
        }
    }

    companion object {
        private const val MAX_PLAYER_SIZE = 10
    }
}