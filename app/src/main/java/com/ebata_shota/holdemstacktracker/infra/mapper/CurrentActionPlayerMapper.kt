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
     * @param playerOrder プレイヤーの順番
     * @param btnPlayerId ボタンプレイヤー
     * @param phaseStateList
     */
    fun mapCurrentActionPlayerId(
        playerOrder: List<PlayerId>,
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
        // lastActionPlayerId がnullの場合は
        // つまり、誰もアクションしていないフェーズの開始時
        // BTNの次の人からアクションを開始する
        val lastPlayerId = lastActionPlayerId ?: btnPlayerId
        val lastActionPlayerIndex = playerOrder.indexOf(lastPlayerId).apply {
            if (this@apply == -1) {
                throw IllegalStateException("lastPlayerId=$lastPlayerId がplayerOrderに存在しない")
            }
        }
        // lastPlayerId の次のプレイヤーのIDが現在のアクションプレイヤー
        // リストの最後に到達したら先頭に戻る
        val nextIndex = (lastActionPlayerIndex + 1) % playerOrder.size
        return playerOrder[nextIndex]
    }
}