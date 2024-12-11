package com.ebata_shota.holdemstacktracker.infra.model

import androidx.annotation.Keep
import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction

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

        fun of(action: Action): BetPhaseActionType {
            return when (action) {
                is BetPhaseAction.Blind -> Blind
                is BetPhaseAction.Fold -> Fold
                is BetPhaseAction.Check -> Check
                is BetPhaseAction.Call -> Call
                is BetPhaseAction.Bet -> Bet
                is BetPhaseAction.Raise -> Raise
                is BetPhaseAction.AllIn -> AllIn
                is BetPhaseAction.AllInSkip -> AllInSkip
                is BetPhaseAction.FoldSkip -> FoldSkip
            }
        }
    }
}