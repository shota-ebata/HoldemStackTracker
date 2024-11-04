package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class IsActionRequiredUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase
) : IsActionRequiredUseCase {
    override fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseActionState>
    ): Boolean {
        // 全員の最後のアクションを一つづつ取得
        val playerOrderSize = playerOrder.size
        val lastActionList = actionStateList.takeLast(playerOrderSize)
        // コールベットサイズ
        val callBetSize: Float = getMaxBetSize.invoke(lastActionList)
        // アクション数がプレイヤー人数より少ないなら、アクションが必要な人がいる
        if (lastActionList.size < playerOrderSize) {
            return true
        }
        // アクションが必要な人がいるか？
        return lastActionList.any { action ->
            when (action) {
                // オールインはこれ以上アクションできない判断
                is BetPhaseActionState.AllIn -> false
                // AllIn以外のベットアクションでコールに必要なベットサイズと異なる場合は、アクションが必要
                is BetPhaseActionState.BetAction -> action.betSize != callBetSize
                // コールに必要なベットサイズが0.0より大きい場合checkでは不足している
                is BetPhaseActionState.Check -> callBetSize > 0.0f
                is BetPhaseActionState.Fold -> false
                is BetPhaseActionState.FoldSkip -> false
                is BetPhaseActionState.AllInSkip -> false
            }
        }
    }

}