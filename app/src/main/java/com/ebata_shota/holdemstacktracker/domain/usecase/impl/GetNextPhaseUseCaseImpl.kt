package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.AllInOpen
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.End
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Flop
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.PotSettlement
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.River
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.ShowDown
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Standby
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Turn
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
        phaseStateList: List<PhaseState>
    ): PhaseState {
        return when (val latestPhase: PhaseState? = phaseStateList.lastOrNull()) {
            is Standby -> PreFlop(actionStateList = emptyList())
            is BetPhase -> getNextPhaseStateFromBetPhase(playerOrder, phaseStateList, latestPhase)
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
        phaseStateList: List<PhaseState>,
        latestPhase: BetPhase
    ): PhaseState {
        // プレイヤーそれぞれの最後のAction
        val lastActions: Map<PlayerId, BetPhaseActionState?> = getPlayerLastActions.invoke(playerOrder, phaseStateList)
        // 降りてないプレイヤー人数
        val activePlayerCount = lastActions.count { (_, lastAction) ->
            lastAction !is BetPhaseActionState.FoldSkip && lastAction !is BetPhaseActionState.Fold
        }
        // 降りてないプレイヤーが2人未満（基本的には、1人を除いてFoldしている状態）
        // の場合は決済フェーズへ
        if (activePlayerCount < 2) {
            return PotSettlement
        }
        // AllInプレイヤー人数
        val allInPlayerCount = lastActions.count { (_, lastAction) ->
            lastAction is BetPhaseActionState.AllIn || lastAction is BetPhaseActionState.AllInSkip
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