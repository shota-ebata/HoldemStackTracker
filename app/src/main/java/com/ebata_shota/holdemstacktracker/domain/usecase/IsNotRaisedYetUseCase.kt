package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction

fun interface IsNotRaisedYetUseCase {
    suspend operator fun invoke(actionStateList: List<BetPhaseAction>): Boolean
}
