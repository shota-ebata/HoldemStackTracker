package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import java.time.Instant
import javax.inject.Inject

class MovePositionUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository
) : MovePositionUseCase {
    override suspend fun invoke(
        playerId: PlayerId,
        table: Table,
        movePosition: MovePosition
    ) {
        val playerOrder = table.playerOrder
        val currentIndex = playerOrder.indexOf(playerId)
        val index = when (movePosition) {
            MovePosition.PREV -> {
                if (currentIndex - 1 in 0..playerOrder.lastIndex) {
                    currentIndex - 1
                } else {
                    table.playerOrder.lastIndex
                }
            }

            MovePosition.NEXT -> {
                if (currentIndex + 1 in 0..playerOrder.lastIndex) {
                    currentIndex + 1
                } else {
                    0
                }
            }
        }
        val movedPlayerOrder = moveItem(
            list = playerOrder.toMutableList(),
            fromIndex = currentIndex,
            toIndex = index
        )

        val copiedTable = table.copy(
            playerOrder = movedPlayerOrder,
            updateTime = Instant.now(),
            version = table.version + 1
        )
        tableRepository.sendTable(copiedTable)
    }

    private fun <T> moveItem(list: MutableList<T>, fromIndex: Int, toIndex: Int): List<T> {
        // アイテムを取り出してから削除
        val item = list.removeAt(fromIndex)
        // 指定されたインデックスに挿入
        list.add(toIndex, item)
        return list
    }
}