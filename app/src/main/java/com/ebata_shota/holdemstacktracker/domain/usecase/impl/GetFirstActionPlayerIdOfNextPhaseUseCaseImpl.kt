package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetFirstActionPlayerIdOfNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRequiredActionPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getSortedByActionOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetFirstActionPlayerIdOfNextPhaseUseCaseImpl
@Inject
constructor(
    private val getRequiredActionPlayerIds: GetRequiredActionPlayerIdsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetFirstActionPlayerIdOfNextPhaseUseCase {

    /**
     * 次のフェーズで、最初にアクションするプレイヤーのIDを取得する
     */
    override suspend fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): PlayerId? = withContext(dispatcher) {
        val currentPhase = currentGame.phaseList.lastOrNull()
        return@withContext when (currentPhase) {
            is Phase.Standby -> {
                if (playerOrder.size == 2) {
                    // 2人しかいない場合
                    // 次のフェーズであるPreFlopの最初のアクションプレイヤーは
                    // BTNからSBを始めるのでnextPlayerはBTNとなる。
                    btnPlayerId
                } else {
                    // 3人以上なら普通に最初のPlayerを返す
                    getFirstActionPlayerId(
                        btnPlayerId = btnPlayerId,
                        playerOrder = playerOrder,
                        currentGame = currentGame
                    )
                }
            }

            is Phase.PreFlop,
            is Phase.Flop,
            is Phase.Turn,
                -> {
                // 次のフェーズはベットフェーズなので普通に最初のプレイヤーIDを返す
                getFirstActionPlayerId(
                    btnPlayerId = btnPlayerId,
                    playerOrder = playerOrder,
                    currentGame = currentGame
                )
            }
            is Phase.River,
            is Phase.PotSettlement,
            is Phase.End,
            null,
                -> null
        }
    }

    /**
     * アクション可能なプレイヤーの中から
     * 最初にアクションするプレイヤーIDを取得する
     */
    private suspend fun getFirstActionPlayerId(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): PlayerId? {
        // アクションが必要な人の一覧を取得する
        val requiredActionPlayerIds = getRequiredActionPlayerIds.invoke(
            btnPlayerId = btnPlayerId,
            playerOrder = playerOrder,
            currentGame = currentGame
        )
        // それをアクション順でソートする
        val sortedRequiredActionPlayerIds = playerOrder
            .getSortedByActionOrder(btnPlayerId = btnPlayerId)
            .mapNotNull { playerId ->
                requiredActionPlayerIds.find { it == playerId }
            }
        // その最初の一人が次のフェーズでアクションする人となる
        return sortedRequiredActionPlayerIds.firstOrNull()
    }
}