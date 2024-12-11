package com.ebata_shota.holdemstacktracker.infra.model

import androidx.annotation.Keep
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState

@Keep
enum class BetPhaseActionType {
    Blind,
    Fold,
    Check,
    Call,
    Bet,
    Raise,
    AllIn,
    AllInSkip,
    FoldSkip;

    companion object {

        fun of(label: String): BetPhaseActionType {
            return entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }

        fun of(actionState: ActionState): BetPhaseActionType {
            return when (actionState) {
                is BetPhaseActionState.Blind -> Blind
                is BetPhaseActionState.Fold -> Fold
                is BetPhaseActionState.Check -> Check
                is BetPhaseActionState.Call -> Call
                is BetPhaseActionState.Bet -> Bet
                is BetPhaseActionState.Raise -> Raise
                is BetPhaseActionState.AllIn -> AllIn
                is BetPhaseActionState.AllInSkip -> AllInSkip
                is BetPhaseActionState.FoldSkip -> FoldSkip
            }
        }
    }
}