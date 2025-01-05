package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentPlayerIdUseCaseImpl
@Inject constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetCurrentPlayerIdUseCase {

    /**
     * 現在のゲーム状態[Game]で
     * プレイするべきプレイヤーを返す。
     */
    override suspend fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentBetPhase: Phase.BetPhase
    ): PlayerId = withContext(dispatcher) {
        // 全員の最後のアクションを一つづつ取得
        val actionStateList = currentBetPhase.actionStateList
        // 最後にアクションしたプレイヤーIDを取得（アクションがない場合はBTN）
        val latestActionPlayerId = actionStateList.lastOrNull()?.playerId ?: btnPlayerId
        // 最後にアクションしたプレイヤーIDのindexを取得
        val latestActionPlayerIndex = playerOrder.indexOf(latestActionPlayerId)
        val nextPlayerIndex: Int = if (
            playerOrder.size == 2
            && actionStateList.isEmpty()
            && currentBetPhase is Phase.PreFlop
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
        return@withContext playerOrder[nextPlayerIndex]
    }
}