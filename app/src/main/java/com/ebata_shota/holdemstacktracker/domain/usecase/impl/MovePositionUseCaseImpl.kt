package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import javax.inject.Inject

class MovePositionUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : MovePositionUseCase {

    // TODO: UT書きたい
    override suspend fun invoke(
        playerId: PlayerId,
        table: Table,
        movePosition: MovePosition,
    ) {
        val playerOrder = table.playerOrder
        val playerOrderWithoutLeaved = table.playerOrderWithoutLeaved
        val currentIndex = playerOrder.indexOf(playerId)
        val toIndex = when (movePosition) {
            MovePosition.PREV -> {
                // プレイ中のプレイヤーリスト内での現在のインデックスを取得
                val currentPlayerIndexInLeaved = playerOrderWithoutLeaved.indexOf(playerId)
                // プレイ中のプレイヤーリスト内で、1つ前のプレイヤーのインデックスを計算
                val prevPlayerIndexInLeaved = if (currentPlayerIndexInLeaved - 1 >= 0) {
                    currentPlayerIndexInLeaved - 1
                } else {
                    playerOrderWithoutLeaved.lastIndex
                }

                // 1つ前のプレイヤーのIDを取得
                val prevPlayerId = playerOrderWithoutLeaved[prevPlayerIndexInLeaved]
                // 全員のリスト（playerOrder）での、そのプレイヤーのインデックスを入れ替え先とする
                playerOrder.indexOf(prevPlayerId)
            }

            MovePosition.NEXT -> {
                // プレイ中のプレイヤーリスト内での現在のインデックスを取得
                val currentPlayerIndexInLeaved = playerOrderWithoutLeaved.indexOf(playerId)
                // プレイ中のプレイヤーリスト内で、1つ後のプレイヤーのインデックスを計算
                val nextPlayerIndexInLeaved = if (currentPlayerIndexInLeaved + 1 <= playerOrderWithoutLeaved.lastIndex) {
                    currentPlayerIndexInLeaved + 1
                } else {
                    0
                }
                // 1つ後のプレイヤーのIDを取得
                val nextPlayerId = playerOrderWithoutLeaved[nextPlayerIndexInLeaved]
                // 全員のリスト（playerOrder）での、そのプレイヤーのインデックスを入れ替え先とする
                playerOrder.indexOf(nextPlayerId)
            }
        }
        if (currentIndex == toIndex) {
            // 変化ないので更新しない
            return
        }
        val movedPlayerOrder = moveItem(
            list = playerOrder.toMutableList(),
            fromIndex = currentIndex,
            toIndex = toIndex
        )
        tableRepository.updatePlayerOrder(
            tableId = table.id,
            playerOrder = movedPlayerOrder
        )
    }

    private fun <T> moveItem(list: MutableList<T>, fromIndex: Int, toIndex: Int): List<T> {
        // アイテムを取り出してから削除
        val item = list.removeAt(fromIndex)
        // 指定されたインデックスに挿入
        list.add(toIndex, item)
        return list
    }
}