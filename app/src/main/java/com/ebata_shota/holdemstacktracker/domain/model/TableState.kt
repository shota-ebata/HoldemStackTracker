package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class TableState(
    val id: Long,
    val version: Int,
    val name: String,
    val hostPlayerId: Long,
    val players: List<PlayerState>,
    val podInfoList: List<PodState>,
    val playerOrder: List<Long>,
    val btnPlayerId: Long,
    val phaseStateList: List<PhaseState>,
    val ruleStatus: RuleState,
    val startTime: LocalDateTime
) {
    val currentActionPlayerId: Long? = phaseStateList.lastOrNull()?.let { phaseState ->
        val lastActionPlayerId: Long? = when (phaseState) {
            is PhaseState.BetPhase -> phaseState.actionStateList.lastOrNull()?.playerId
            else -> return@let null
        }
        if (lastActionPlayerId != null) {
            // 最後にアクションした人の次の人が
            // 現在のアクションを行うプレイヤー
            getNextActionPlayerId(playerOrder, lastActionPlayerId)
        } else {
            // lastActionPlayerIdがnull
            // つまり、だれもアクションしていないフェーズの開始時
            // BTNの次の人からアクションを開始する
            getNextActionPlayerId(playerOrder, btnPlayerId)
        }
    }

    private fun getNextActionPlayerId(playerOrder: List<Long>, basePlayerId: Long?): Long {
        val playerOrderLastIndex = playerOrder.lastIndex
        if (playerOrderLastIndex == -1) {
            throw IllegalStateException("playerOrderが空")
        }
        val lastActionPlayerIndex = playerOrder.indexOf(basePlayerId).apply {
            if (this@apply == -1) {
                throw IllegalStateException("basePlayerId=$basePlayerId がplayerOrderに存在しない")
            }
        }
        // basePlayerIdの次のプレイヤーのIndexを返す
        return if (playerOrderLastIndex == lastActionPlayerIndex) {
            // 最後なら0
            playerOrder[0]
        } else {
            playerOrder[lastActionPlayerIndex + 1]
        }
    }
}


