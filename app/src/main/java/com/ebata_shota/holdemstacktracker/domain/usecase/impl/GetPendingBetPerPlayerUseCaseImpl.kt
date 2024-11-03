package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import javax.inject.Inject

class GetPendingBetPerPlayerUseCaseImpl
@Inject
constructor(
    private val getMaxBetSizeUseCase: GetMaxBetSizeUseCase
) : GetPendingBetPerPlayerUseCase {
    override fun invoke(playerOrder: List<PlayerId>, actionStateList: List<BetPhaseActionState>): Map<PlayerId, Float> {
        return playerOrder.associateWith { playerId ->
            // このフェーズでの、特定プレイヤーのアクション一覧を取得
            val playerActionList = actionStateList.filter { it.playerId == playerId }
            // プレイヤーの最大ベットサイズ = そのプレイヤーの最終的なベットサイズ
            getMaxBetSizeUseCase.invoke(playerActionList)
        }.filter { it.value > 0.0f }
    }
}