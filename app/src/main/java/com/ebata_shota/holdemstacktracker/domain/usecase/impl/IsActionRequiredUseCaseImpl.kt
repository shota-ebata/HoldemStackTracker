package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class IsActionRequiredUseCaseImpl
@Inject
constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getMaxBetSizeUseCase: GetMaxBetSizeUseCase
) : IsActionRequiredUseCase {
    override fun invoke(latestTableState: TableState): Boolean {
        val latestPhase: BetPhase = getLatestBetPhase.invoke(latestTableState)
        // 全員の最後のアクションを一つづつ取得
        val playerOrderSize = latestTableState.playerOrder.size
        val lastActionList = latestPhase.actionStateList.takeLast(playerOrderSize)
        // コールベットサイズ
        val callBetSize: Float = getMaxBetSizeUseCase.invoke(lastActionList)
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