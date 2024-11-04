package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPodStateListUseCase
import javax.inject.Inject
import kotlin.random.Random

class GetPodStateListUseCaseImpl
@Inject
constructor() : GetPodStateListUseCase {
    override fun invoke(
        podStateList: List<PodState>,
        pendingBetPerPlayer: Map<PlayerId, Double>
    ): List<PodState> {
        // ポッドに入っていないベットが残っているプレイヤー数
        val pendingBetPlayerCount: Int = pendingBetPerPlayer.size
        return if (pendingBetPlayerCount > 0) {
            // 1人以上の場合、ポッドに入れていく
            getNewPodStateList(
                podStateList = podStateList,
                pendingBetPerPlayer = pendingBetPerPlayer
            )
        } else {
            // ポッドに入っていないベットが残っているプレイヤー数0人の場合、ポッドはそのまま。
            podStateList
        }
    }

    private fun getNewPodStateList(
        podStateList: List<PodState>,
        pendingBetPerPlayer: Map<PlayerId, Double>
    ): List<PodState> {
        // 最新のポッドを取得
        val lastPodState: PodState? = podStateList.lastOrNull()
        var currentPodState: PodState = if (lastPodState != null && lastPodState.isClosed.not()) {
            // 以前のポッドが閉じていない場合は、そのポッドを使う
            lastPodState
        } else {
            // 以前のポッドがない、もしくは閉じている場合は、新しいポッドを作成する
            val nextPodNumber = lastPodState?.podNumber?.plus(1) ?: 0
            createPodState(nextPodNumber)
        }
        var podSize: Double = currentPodState.podSize
        val involvedPlayerIds = currentPodState.involvedPlayerIds.toMutableList()

        val minBetSize = pendingBetPerPlayer.map { it.value }.min()
        val updatedPendingPrePlayer = pendingBetPerPlayer.mapValues { (playerId, betSize) ->
            // ポッドに入れる
            podSize += minBetSize
            // ポッドの関与者になる
            involvedPlayerIds.add(playerId)
            // Podに入れた分だけ、ベットサイズから減らす
            betSize - minBetSize
        }.filter { it.value > 0.0 } // ポッドに入っていないベットが残っている人でフィルタリングする
        // まだポッドに入っていないベットを持っている人がいる場合、このポッドはcloseする
        val isClosed = updatedPendingPrePlayer.isNotEmpty()
        currentPodState = currentPodState.copy(
            podSize = podSize,
            involvedPlayerIds = involvedPlayerIds.distinct(), // 重複を無くして上書き
            isClosed = isClosed
        )
        val updatedPodStateList = podStateList.toMutableList()
        val index = updatedPodStateList.indexOfFirstOrNull { it.id == currentPodState.id }
        if (index != null) {
            // 既存ポッドがあれば上書き
            updatedPodStateList[index] = currentPodState
        } else {
            // 新規ポッドであれば追加
            updatedPodStateList.add(currentPodState)
        }
        if (updatedPendingPrePlayer.isEmpty()) {
            // もうベットが無いなら、現在のpodを返す
            return updatedPodStateList
        }
        // まだ残っている可能性があるので再帰
        return getNewPodStateList(updatedPodStateList, updatedPendingPrePlayer)
    }

    private fun createPodState(podNumber: Int) = PodState(
        id = createPodId(),
        podNumber = podNumber,
        podSize = 0.0,
        involvedPlayerIds = emptyList(),
        isClosed = false
    )

    private fun createPodId(): Long {
        return Random.nextLong()
    }
}