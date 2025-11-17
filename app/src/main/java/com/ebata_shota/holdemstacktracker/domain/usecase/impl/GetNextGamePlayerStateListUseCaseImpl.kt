package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGamePlayerStateListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextGamePlayerStateListUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
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
        pendingBetPerPlayer: Map<PlayerId, Int>,
        players: List<GamePlayer>,
        action: BetPhaseAction,
    ): List<GamePlayer> = withContext(dispatcher) {
        return@withContext when (action) {
            is BetPhaseAction.BetAction -> {
                // ベットアクションならスタックを減らす

                return@withContext players.map { gamePlayer ->
                    if (gamePlayer.id == action.playerId) {
                        val latestBetSize: Int = pendingBetPerPlayer[action.playerId] ?: 0
                        gamePlayer.copy(stack = gamePlayer.stack + latestBetSize - action.betSize)
                    } else {
                        gamePlayer
                    }
                }
            }

            else -> players
        }
    }
}