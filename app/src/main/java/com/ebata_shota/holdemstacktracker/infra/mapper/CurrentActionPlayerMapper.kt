package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentActionPlayerMapper
@Inject
constructor() {
    /**
     * アクション履歴（phaseStateList）から
     * 現在のアクションプレイヤーを決める
     *
     * @param playerOrderWithoutLeavedPlayer プレイヤーの順番（離席プレイヤーを除いた）
     * @param btnPlayerId ボタンプレイヤー
     * @param phaseStateList
     */
    fun mapCurrentActionPlayerId(
        playerOrderWithoutLeavedPlayer: List<PlayerId>,
        btnPlayerId: PlayerId,
        phaseStateList: List<PhaseState>
    ): PlayerId? = phaseStateList.lastOrNull()?.let { phaseState ->
        val lastActionPlayerId: PlayerId? = when (phaseState) {
            is PhaseState.BetPhase -> phaseState.actionStateList.lastOrNull()?.playerId
            else -> return@let null
        }
        // 最後にアクションした人の次の人が
        // 現在のアクションを行うプレイヤー
        // else
        // lastActionPlayerIdがnullの場合は
        // つまり、誰もアクションしていないフェーズの開始時
        // BTNの次の人からアクションを開始する
        getCurrentActionPlayerId(
            playerOrder = playerOrderWithoutLeavedPlayer,
            basePlayerId = lastActionPlayerId ?: btnPlayerId
        )
    }

    private fun getCurrentActionPlayerId(playerOrder: List<PlayerId>, basePlayerId: PlayerId?): PlayerId {
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