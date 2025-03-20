package com.ebata_shota.holdemstacktracker.domain.extension

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun List<PlayerId>.getNextPlayerId(playerId: PlayerId): PlayerId {
    return this[(indexOf(playerId) + 1) % this.size]
}