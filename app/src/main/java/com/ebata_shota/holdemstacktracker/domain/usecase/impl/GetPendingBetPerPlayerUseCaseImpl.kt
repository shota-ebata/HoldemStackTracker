package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import javax.inject.Inject

class GetPendingBetPerPlayerUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase
) : GetPendingBetPerPlayerUseCase {

    /**
     * まだ、ポッドに入っていない
     * Betされているものをプレイヤーごとに返却
     */
    override fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseAction>
    ): Map<PlayerId, Double> {
        return playerOrder.associateWith { playerId ->
            // このフェーズでの、特定プレイヤーのアクション一覧を取得
            val playerActionList = actionStateList.filter { it.playerId == playerId }
            // プレイヤーの最大ベットサイズ = そのプレイヤーの最終的なベットサイズ
            getMaxBetSize.invoke(playerActionList)
        }.filter { it.value > 0.0 }
    }
}