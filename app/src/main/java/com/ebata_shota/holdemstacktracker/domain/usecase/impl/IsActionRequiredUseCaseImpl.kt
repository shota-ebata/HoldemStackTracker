package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSize
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class IsActionRequiredUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getPendingBetSize: GetPendingBetSize,
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
        // Blindを除いたアクション一覧
        val lastActionListWithoutBlind = lastActionList
            .filterNot { it is BetPhaseAction.Blind } // Blindは除く
        // アクション数がプレイヤー人数より少ないなら、アクションが必要な人がいる
        if (lastActionListWithoutBlind.size < playerOrderSize) {
            return true
        }
        // アクションが必要な人がいるか？
        return lastActionList.any { action ->
            when (action) {
                // オールインはこれ以上アクションできない判断
                is BetPhaseAction.AllIn -> false
                // AllIn以外のベットアクションでコールに必要なベットサイズと異なる場合は、アクションが必要
                is BetPhaseAction.BetAction -> {
                    action.betSize != callBetSize
                }
                // その人がすでに賭けているベット額がコール額と一致していないなら、アクションが必要
                is BetPhaseAction.Check -> {
                    val pendingBet = getPendingBetSize.invoke(
                        actionList = actionStateList,
                        playerOrder = playerOrder,
                        playerId = action.playerId
                    )
                    pendingBet != callBetSize
                }
                is BetPhaseAction.Fold -> false
                is BetPhaseAction.FoldSkip -> false
                is BetPhaseAction.AllInSkip -> false
            }
        }
    }

}