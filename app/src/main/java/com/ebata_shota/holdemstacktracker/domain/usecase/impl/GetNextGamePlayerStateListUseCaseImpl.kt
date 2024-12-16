package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
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
        players: Set<GamePlayer>,
        action: BetPhaseAction
    ): Set<GamePlayer> {
        val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
        return when (action) {
            is BetPhaseAction.BetAction -> {
                // ベットアクションならスタックを減らす

                return players.map { gamePlayer ->
                    if (gamePlayer.id == action.playerId) {
                        val latestBetSize: Double = pendingBetPerPlayer[myPlayerId] ?: 0.0
                        gamePlayer.copy(stack = gamePlayer.stack + latestBetSize - action.betSize)
                    } else {
                        gamePlayer
                    }
                }.toSet()
            }

            else -> players
        }
    }
}