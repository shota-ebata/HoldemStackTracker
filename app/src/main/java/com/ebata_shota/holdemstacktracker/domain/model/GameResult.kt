package com.ebata_shota.holdemstacktracker.domain.model

data class GameResult(
    val potResults: List<PotResult>
) {
    data class PotResult(
        val id: PotId,
        val potNumber: Int,
        val potSize: Int,
        val winnerPlayerIds: List<PlayerId>,
    )
}