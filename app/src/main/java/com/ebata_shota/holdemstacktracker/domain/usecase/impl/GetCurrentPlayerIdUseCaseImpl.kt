package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import javax.inject.Inject

class GetCurrentPlayerIdUseCaseImpl
@Inject constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase
) : GetCurrentPlayerIdUseCase {

    /**
     * 現在のゲーム状態[Game]で
     * プレイするべきプレイヤーを返す。
     */
    override fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        game: Game
    ): PlayerId {
        // 全員の最後のアクションを一つづつ取得
        val latestBetPhase = getLatestBetPhase.invoke(game)
        // 全員の最後のアクションを一つづつ取得
        val actionStateList = latestBetPhase.actionStateList
        // 最後にアクションしたプレイヤーIDを取得（アクションがない場合はBTN）
        val latestActionPlayerId = actionStateList.lastOrNull()?.playerId ?: btnPlayerId
        // 最後にアクションしたプレイヤーIDのindexを取得
        val latestActionPlayerIndex = playerOrder.indexOf(latestActionPlayerId)
        val nextPlayerIndex: Int = if (
            playerOrder.size == 2
            && actionStateList.isEmpty()
            && latestBetPhase is Phase.PreFlop
        ) {
            // 2人しかいない
            // かつ、まだ誰もアクションしていない
            // かつ、PreFlopの場合
            // BTNからSBを始めるのでnextPlayerはBTNとなる。
            playerOrder.indexOf(btnPlayerId)
        } else {
            // 基本は最後にアクションした人の次の人
            (latestActionPlayerIndex + 1) % playerOrder.size
        }
        return playerOrder[nextPlayerIndex]
    }
}