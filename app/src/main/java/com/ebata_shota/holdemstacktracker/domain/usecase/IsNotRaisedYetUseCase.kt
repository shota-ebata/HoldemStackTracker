package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction

interface IsNotRaisedYetUseCase {
    fun invoke(actionStateList: List<BetPhaseAction>): Boolean
}