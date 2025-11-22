package com.ebata_shota.holdemstacktracker.domain.model

sealed interface AutoCheckOrFoldType {
    data object None : AutoCheckOrFoldType
    data class ByGame(val gameId: GameId) : AutoCheckOrFoldType
}
