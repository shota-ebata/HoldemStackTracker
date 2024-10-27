package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentActionPlayerMapper
@Inject
constructor() {
    fun mapCurrentActionPlayerId(
        playerOrder: List<Long>,
        basePlayerId: Long,
        phaseStateList: List<PhaseState>
    ): Long? = phaseStateList.lastOrNull()?.let { phaseState ->
        val lastActionPlayerId: Long? = when (phaseState) {
            is PhaseState.BetPhase -> phaseState.actionStateList.lastOrNull()?.playerId
            else -> return@let null
        }
        // 最後にアクションした人の次の人が
        // 現在のアクションを行うプレイヤー
        // else
        // lastActionPlayerIdがnullの場合は
        // つまり、だれもアクションしていないフェーズの開始時
        // BTNの次の人からアクションを開始する
        getCurrentActionPlayerId(
            playerOrder = playerOrder,
            basePlayerId = lastActionPlayerId ?: basePlayerId
        )
    }

    private fun getCurrentActionPlayerId(playerOrder: List<Long>, basePlayerId: Long?): Long {
        val playerOrderLastIndex = playerOrder.lastIndex
        if (playerOrderLastIndex == -1) {
            throw IllegalStateException("playerOrderが空")
        }
        val lastActionPlayerIndex = playerOrder.indexOf(basePlayerId).apply {
            if (this@apply == -1) {
                throw IllegalStateException("basePlayerId=$basePlayerId がplayerOrderに存在しない")
            }
        }
        // basePlayerIdの次のプレイヤーのIDを返す
        return if (playerOrderLastIndex == lastActionPlayerIndex) {
            // 最後なら0
            playerOrder[0]
        } else {
            playerOrder[lastActionPlayerIndex + 1]
        }
    }
}