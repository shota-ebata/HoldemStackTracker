package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.model.PotId
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPotListUseCaseImpl
@Inject
constructor(
    private val randomIdRepository: RandomIdRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetPotListUseCase {

    override suspend fun invoke(
        updatedPlayers: List<GamePlayer>,
        potList: List<Pot>,
        pendingBetPerPlayerWithoutZero: Map<PlayerId, Int>,
        activePlayerIds: List<PlayerId>,
    ): List<Pot> = withContext(dispatcher) {
        // ポットに入っていないベットが残っているプレイヤー数
        val pendingBetPlayerCount: Int = pendingBetPerPlayerWithoutZero.size
        return@withContext if (pendingBetPlayerCount > 0) {
            // 1人以上の場合、ポットに入れていく
            getNewPotList(
                updatedPlayers = updatedPlayers,
                potList = potList,
                pendingBetPerPlayer = pendingBetPerPlayerWithoutZero,
                activePlayerIds = activePlayerIds
            )
        } else {
            // ポットに入っていないベットが残っているプレイヤー数0人の場合、ポットはそのまま。
            potList
        }
    }

    private fun getNewPotList(
        updatedPlayers: List<GamePlayer>,
        potList: List<Pot>,
        pendingBetPerPlayer: Map<PlayerId, Int>,
        activePlayerIds: List<PlayerId>,
    ): List<Pot> {
        // 最新のポットを取得
        val lastPot: Pot? = potList.lastOrNull()
        var currentPot: Pot = if (lastPot != null && lastPot.isClosed.not()) {
            // 以前のポットが閉じていない場合は、そのポットを使う
            lastPot
        } else {
            // 以前のポットがない、もしくは閉じている場合は、新しいポットを作成する
            val nextPotNumber = lastPot?.potNumber?.plus(1) ?: 0
            createPot(nextPotNumber)
        }
        var potSize: Int = currentPot.potSize
        val involvedPlayerIds = currentPot.involvedPlayerIds.toMutableList()

        // 降りてない人の最低ベットサイズを取得
        val minBetSize = pendingBetPerPlayer.filter { (key, _) ->
            activePlayerIds.any { it == key}
        }.map {
            it.value
        }.min()
        val updatedPendingPrePlayer = pendingBetPerPlayer.mapValues { (playerId, betSize) ->
            // ポットにいれるサイズを決める
            val isActivePlayer = activePlayerIds.any { it == playerId }
            val addSize = if (isActivePlayer) {
                // 降りてないプレイヤーは一番小さいベットを一旦入れる
                minBetSize
            } else {
                // 降りている人は、すべていれる
                betSize
            }
            // ポットに入れる
            potSize += addSize
            // ポットの関与者になる
            involvedPlayerIds.add(playerId)
            // Potに入れた分だけ、ベットサイズから減らす
            betSize - addSize
        }.filter { it.value > 0 } // ポットに入っていないベットが残っている人でフィルタリングする
        // まだポットに入っていないベットを持っている人がいる場合、
        // もしくは、もしこのpotの関与者のスタックが0になっているなら
        // このポットはcloseする
        val isEmptyPotInvolvedPlayerStack: Boolean = involvedPlayerIds.any { involvedPlayerId ->
            val updatedPlayer = updatedPlayers.find { it.id == involvedPlayerId }
            updatedPlayer?.stack == 0
        }
        val isClosed = updatedPendingPrePlayer.isNotEmpty() || isEmptyPotInvolvedPlayerStack
        currentPot = currentPot.copy(
            potSize = potSize,
            involvedPlayerIds = involvedPlayerIds.distinct(), // 重複を無くして上書き
            isClosed = isClosed
        )
        val updatedPotList = potList.toMutableList()
        val index = updatedPotList.indexOfFirstOrNull { it.id == currentPot.id }
        if (index != null) {
            // 既存ポットがあれば上書き
            updatedPotList[index] = currentPot
        } else {
            // 新規ポットであれば追加
            updatedPotList.add(currentPot)
        }
        if (updatedPendingPrePlayer.isEmpty()) {
            // もうベットが無いなら、ポットを返す
            // 現在のpotを返す
            return updatedPotList
        }
        // まだ残っている可能性があるので再帰
        return getNewPotList(
            updatedPlayers = updatedPlayers,
            potList = updatedPotList,
            pendingBetPerPlayer = updatedPendingPrePlayer,
            activePlayerIds = activePlayerIds
        )
    }

    private fun createPot(potNumber: Int) = Pot(
        id = PotId(value = randomIdRepository.generateRandomId()),
        potNumber = potNumber,
        potSize = 0,
        involvedPlayerIds = emptyList(),
        isClosed = false
    )
}