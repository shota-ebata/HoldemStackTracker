package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStateListUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetNextPlayerStateListUseCaseImpl
@Inject
constructor(
    private val prefRepository: PrefRepository
) : GetNextPlayerStateListUseCase {
    /**
     * ベット状況(pendingBetPerPlayer)に合わせて
     * Actionに応じたプレイヤーのスタックを更新して返す
     *
     * @param pendingBetPerPlayer 確定していないベット状況
     * @param players プレイヤーの情報
     * @param action 今回のアクション
     */
    override suspend fun invoke(
        pendingBetPerPlayer: Map<PlayerId, Float>,
        players: List<PlayerState>,
        action: ActionState
    ): List<PlayerState> {
        val myPlayerId = PlayerId(prefRepository.myPlayerId.first())
        val myPlayerStateIndex = players.indexOfFirstOrNull { it.id == myPlayerId }
            ?: throw IllegalStateException("Player not found")
        return when (action) {
            is ActionState.BetAction -> {
                // ベットアクションならスタックを減らす
                players.mapAtIndex(myPlayerStateIndex) {
                    val latestBetSize: Float = pendingBetPerPlayer[myPlayerId] ?: 0.0f
                    // 最後のベットアクションとの差分をスタックから減らす
                    it.copy(stack = it.stack + latestBetSize - action.betSize)
                }
            }

            else -> players
        }
    }
}