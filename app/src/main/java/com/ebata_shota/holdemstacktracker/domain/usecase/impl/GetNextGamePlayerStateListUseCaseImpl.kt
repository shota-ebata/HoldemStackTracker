package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGamePlayerStateListUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetNextGamePlayerStateListUseCaseImpl
@Inject
constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : GetNextGamePlayerStateListUseCase {
    /**
     * ベット状況(pendingBetPerPlayer)に合わせて
     * Actionに応じたプレイヤーのスタックを更新して返す
     *
     * @param pendingBetPerPlayer 確定していないベット状況
     * @param players プレイヤーの情報
     * @param action 今回のアクション
     */
    override suspend fun invoke(
        pendingBetPerPlayer: Map<PlayerId, Double>,
        players: List<GamePlayerState>,
        action: BetPhaseActionState
    ): List<GamePlayerState> {
        val myPlayerId = PlayerId(firebaseAuthRepository.uidFlow.first())
        val myPlayerStateIndex = players.indexOfFirstOrNull { it.id == myPlayerId }
            ?: throw IllegalStateException("Player not found")
        return when (action) {
            is BetPhaseActionState.BetAction -> {
                // ベットアクションならスタックを減らす
                players.mapAtIndex(myPlayerStateIndex) {
                    val latestBetSize: Double = pendingBetPerPlayer[myPlayerId] ?: 0.0
                    // 最後のベットアクションとの差分をスタックから減らす
                    it.copy(stack = it.stack + latestBetSize - action.betSize)
                }
            }

            else -> players
        }
    }
}