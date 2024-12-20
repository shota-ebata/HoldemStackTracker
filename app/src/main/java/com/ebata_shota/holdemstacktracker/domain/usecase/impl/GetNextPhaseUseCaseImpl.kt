package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.AllInOpen
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.End
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Flop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PotSettlement
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.River
import com.ebata_shota.holdemstacktracker.domain.model.Phase.ShowDown
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Standby
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Turn
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import javax.inject.Inject

class GetNextPhaseUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase
) : GetNextPhaseUseCase {
    override fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): Phase {
        return when (val latestPhase: Phase? = phaseList.lastOrNull()) {
            is Standby -> PreFlop(actionStateList = emptyList())
            is BetPhase -> getNextPhaseStateFromBetPhase(playerOrder, phaseList, latestPhase)
            is AllInOpen -> PotSettlement
            is ShowDown -> PotSettlement
            is PotSettlement -> End
            is End -> Standby
            null -> {
                // 基本無いはずだが
                Standby
            }
        }
    }

    private fun getNextPhaseStateFromBetPhase(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>,
        latestPhase: BetPhase
    ): Phase {
        // プレイヤーそれぞれの最後のAction
        val lastActions: Map<PlayerId, BetPhaseAction?> = getPlayerLastActions.invoke(playerOrder, phaseList)
        // 降りてないプレイヤー人数
        val activePlayerCount = lastActions.count { (_, lastAction) ->
            lastAction !is BetPhaseAction.FoldSkip && lastAction !is BetPhaseAction.Fold
        }
        // 降りてないプレイヤーが2人未満（基本的には、1人を除いてFoldしている状態）
        // の場合は決済フェーズへ
        if (activePlayerCount < 2) {
            return PotSettlement
        }
        // AllInプレイヤー人数
        val allInPlayerCount = lastActions.count { (_, lastAction) ->
            lastAction is BetPhaseAction.AllIn || lastAction is BetPhaseAction.AllInSkip
        }
        // AllInプレイヤーだけが残っている場合は
        if (allInPlayerCount == activePlayerCount) {
            // AllInOpenへ
            return AllInOpen
        }
        // その他の場合は次のべットフェーズへ
        // (フェーズのアクションがすべて終わっている前提で、このメソッドを呼び出している想定）
        return when (latestPhase) {
            is PreFlop -> Flop(actionStateList = emptyList())
            is Flop -> Turn(actionStateList = emptyList())
            is Turn -> River(actionStateList = emptyList())
            is River -> ShowDown
        }
    }
}