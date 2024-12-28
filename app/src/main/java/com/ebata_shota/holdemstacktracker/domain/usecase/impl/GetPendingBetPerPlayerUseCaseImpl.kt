package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPendingBetPerPlayerUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetPendingBetPerPlayerUseCase {

    /**
     * まだ、ポットに入っていない
     * Betされているものをプレイヤーごとに返却
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseAction>,
    ): Map<PlayerId, Int> = withContext(dispatcher) {
        return@withContext playerOrder.associateWith { playerId ->
            // このフェーズでの、特定プレイヤーのアクション一覧を取得
            val playerActionList = actionStateList.filter { it.playerId == playerId }
            // プレイヤーの最大ベットサイズ = そのプレイヤーの最終的なベットサイズ
            getMaxBetSize.invoke(playerActionList)
        }.filter { it.value > 0 }
    }
}