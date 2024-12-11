package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class IsActionRequiredUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase
) : IsActionRequiredUseCase {

    /**
     * アクションが必要な人がいるか？
     */
    override fun invoke(
        playerOrder: List<PlayerId>,
        actionStateList: List<BetPhaseAction>
    ): Boolean {
        // 全員の最後のアクションを一つづつ取得
        val playerOrderSize = playerOrder.size
        val lastActionList = actionStateList.takeLast(playerOrderSize)
        // コールベットサイズ
        val callBetSize: Double = getMaxBetSize.invoke(lastActionList)
        // アクション数がプレイヤー人数より少ないなら、アクションが必要な人がいる
        if (lastActionList.size < playerOrderSize) {
            return true
        }
        // アクションが必要な人がいるか？
        return lastActionList.any { action ->
            when (action) {
                // オールインはこれ以上アクションできない判断
                is BetPhaseAction.AllIn -> false
                // AllIn以外のベットアクションでコールに必要なベットサイズと異なる場合は、アクションが必要
                is BetPhaseAction.BetAction -> action.betSize != callBetSize
                // コールに必要なベットサイズが0.0より大きい場合checkでは不足している
                is BetPhaseAction.Check -> callBetSize > 0.0
                is BetPhaseAction.Fold -> false
                is BetPhaseAction.FoldSkip -> false
                is BetPhaseAction.AllInSkip -> false
            }
        }
    }

}