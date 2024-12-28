package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotStateListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class GetPotStateListUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPotStateListUseCase {

    override suspend fun invoke(
        potList: List<Pot>,
        pendingBetPerPlayer: Map<PlayerId, Int>
    ): List<Pot> = withContext(dispatcher) {
        // ポットに入っていないベットが残っているプレイヤー数
        val pendingBetPlayerCount: Int = pendingBetPerPlayer.size
        return@withContext if (pendingBetPlayerCount > 0) {
            // 1人以上の場合、ポットに入れていく
            getNewPotStateList(
                potList = potList,
                pendingBetPerPlayer = pendingBetPerPlayer
            )
        } else {
            // ポットに入っていないベットが残っているプレイヤー数0人の場合、ポットはそのまま。
            potList
        }
    }

    private fun getNewPotStateList(
        potList: List<Pot>,
        pendingBetPerPlayer: Map<PlayerId, Int>
    ): List<Pot> {
        // 最新のポットを取得
        val lastPot: Pot? = potList.lastOrNull()
        var currentPot: Pot = if (lastPot != null && lastPot.isClosed.not()) {
            // 以前のポットが閉じていない場合は、そのポットを使う
            lastPot
        } else {
            // 以前のポットがない、もしくは閉じている場合は、新しいポットを作成する
            val nextPotNumber = lastPot?.potNumber?.plus(1L) ?: 0L
            createPotState(nextPotNumber)
        }
        var potSize: Int = currentPot.potSize
        val involvedPlayerIds = currentPot.involvedPlayerIds.toMutableList()

        val minBetSize = pendingBetPerPlayer.map { it.value }.min()
        val updatedPendingPrePlayer = pendingBetPerPlayer.mapValues { (playerId, betSize) ->
            // ポットに入れる
            potSize += minBetSize
            // ポットの関与者になる
            involvedPlayerIds.add(playerId)
            // Potに入れた分だけ、ベットサイズから減らす
            betSize - minBetSize
        }.filter { it.value > 0 } // ポットに入っていないベットが残っている人でフィルタリングする
        // まだポットに入っていないベットを持っている人がいる場合、このポットはcloseする
        val isClosed = updatedPendingPrePlayer.isNotEmpty()
        currentPot = currentPot.copy(
            potSize = potSize,
            involvedPlayerIds = involvedPlayerIds.distinct(), // 重複を無くして上書き
            isClosed = isClosed
        )
        val updatedPotStateList = potList.toMutableList()
        val index = updatedPotStateList.indexOfFirstOrNull { it.id == currentPot.id }
        if (index != null) {
            // 既存ポットがあれば上書き
            updatedPotStateList[index] = currentPot
        } else {
            // 新規ポットであれば追加
            updatedPotStateList.add(currentPot)
        }
        if (updatedPendingPrePlayer.isEmpty()) {
            // もうベットが無いなら、現在のpotを返す
            return updatedPotStateList
        }
        // まだ残っている可能性があるので再帰
        return getNewPotStateList(updatedPotStateList, updatedPendingPrePlayer)
    }

    private fun createPotState(potNumber: Long) = Pot(
        id = createPotId(),
        potNumber = potNumber,
        potSize = 0,
        involvedPlayerIds = emptyList(),
        isClosed = false
    )

    private fun createPotId(): Long {
        return Random.nextLong()
    }
}