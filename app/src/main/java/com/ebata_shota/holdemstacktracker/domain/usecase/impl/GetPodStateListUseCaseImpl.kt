package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pod
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPodStateListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class GetPodStateListUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPodStateListUseCase {

    override suspend fun invoke(
        podList: List<Pod>,
        pendingBetPerPlayer: Map<PlayerId, Double>
    ): List<Pod> = withContext(dispatcher) {
        // ポッドに入っていないベットが残っているプレイヤー数
        val pendingBetPlayerCount: Int = pendingBetPerPlayer.size
        return@withContext if (pendingBetPlayerCount > 0) {
            // 1人以上の場合、ポッドに入れていく
            getNewPodStateList(
                podList = podList,
                pendingBetPerPlayer = pendingBetPerPlayer
            )
        } else {
            // ポッドに入っていないベットが残っているプレイヤー数0人の場合、ポッドはそのまま。
            podList
        }
    }

    private fun getNewPodStateList(
        podList: List<Pod>,
        pendingBetPerPlayer: Map<PlayerId, Double>
    ): List<Pod> {
        // 最新のポッドを取得
        val lastPod: Pod? = podList.lastOrNull()
        var currentPod: Pod = if (lastPod != null && lastPod.isClosed.not()) {
            // 以前のポッドが閉じていない場合は、そのポッドを使う
            lastPod
        } else {
            // 以前のポッドがない、もしくは閉じている場合は、新しいポッドを作成する
            val nextPodNumber = lastPod?.podNumber?.plus(1L) ?: 0L
            createPodState(nextPodNumber)
        }
        var podSize: Double = currentPod.podSize
        val involvedPlayerIds = currentPod.involvedPlayerIds.toMutableList()

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
        currentPod = currentPod.copy(
            podSize = podSize,
            involvedPlayerIds = involvedPlayerIds.distinct(), // 重複を無くして上書き
            isClosed = isClosed
        )
        val updatedPodStateList = podList.toMutableList()
        val index = updatedPodStateList.indexOfFirstOrNull { it.id == currentPod.id }
        if (index != null) {
            // 既存ポッドがあれば上書き
            updatedPodStateList[index] = currentPod
        } else {
            // 新規ポッドであれば追加
            updatedPodStateList.add(currentPod)
        }
        if (updatedPendingPrePlayer.isEmpty()) {
            // もうベットが無いなら、現在のpodを返す
            return updatedPodStateList
        }
        // まだ残っている可能性があるので再帰
        return getNewPodStateList(updatedPodStateList, updatedPendingPrePlayer)
    }

    private fun createPodState(podNumber: Long) = Pod(
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