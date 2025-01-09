package com.ebata_shota.holdemstacktracker.domain.exception

import com.ebata_shota.holdemstacktracker.domain.model.Phase

fun List<Phase>.getLatestBetPhase(): Phase.BetPhase? {
    return findLast { it is Phase.BetPhase } as? Phase.BetPhase
}