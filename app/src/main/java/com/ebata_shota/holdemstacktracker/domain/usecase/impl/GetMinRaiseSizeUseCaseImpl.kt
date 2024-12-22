package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import javax.inject.Inject

class GetMinRaiseSizeUseCaseImpl
@Inject
constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase
) : GetMinRaiseSizeUseCase {

    override fun invoke(
        game: Game,
        minBetSize: Double
    ): Double {
        val betPhase = getLatestBetPhase.invoke(game)
        // 最低引き上げ額
        var minUpliftSize: Double = minBetSize
        // 最高Bet額
        var maxBetSize = 0.0
        betPhase.actionStateList.forEach { betPhaseAction ->
            if (betPhaseAction is BetPhaseAction.BetAction) {
                if (
                    betPhaseAction.betSize > maxBetSize
                    && betPhaseAction.betSize >= minBetSize
                ) {
                    // Bet額の更新があった場合 && それが最低Bet以上なら
                    // その差分を最低引き上げ額とする
                    minUpliftSize = betPhaseAction.betSize - maxBetSize
                    // 最高Bet額を更新する
                    maxBetSize = betPhaseAction.betSize
                }
            }
        }
        return minUpliftSize + maxBetSize
    }
}