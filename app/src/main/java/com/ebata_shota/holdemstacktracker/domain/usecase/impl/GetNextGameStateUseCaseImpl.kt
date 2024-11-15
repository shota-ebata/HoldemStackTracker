package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPodStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class GetNextGameStateUseCaseImpl
@Inject
constructor(
    private val isActionRequired: IsActionRequiredUseCase,
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPodStateList: GetPodStateListUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getNextPlayerStack: GetNextPlayerStackUseCase,
) : GetNextGameStateUseCase {

    override suspend fun invoke(
        latestGameState: GameState,
        action: ActionState
    ): GameState {
        when (action) {
            is BetPhaseActionState -> {
                return getNextGameStateFromBetPhaseAction(latestGameState, action)
            }
        }
    }

    private suspend fun getNextGameStateFromBetPhaseAction(
        latestGameState: GameState,
        action: BetPhaseActionState
    ): GameState {
        // BetPhaseでしかActionはできないので
        val latestPhase: BetPhase = getLatestBetPhase.invoke(latestGameState)
        // まずはActionを追加
        val updatedActionStateList = latestPhase.actionStateList + action
        val latestPhaseStateList: List<PhaseState> = latestGameState.phaseStateList
        // PhaseListの最後の要素を置き換える
        val updatedPhaseStateList: MutableList<PhaseState> = latestPhaseStateList.mapAtIndex(latestPhaseStateList.lastIndex) {
            // Phaseに反映
            when (latestPhase) {
                is PhaseState.PreFlop -> latestPhase.copy(actionStateList = updatedActionStateList)
                is PhaseState.Flop -> latestPhase.copy(actionStateList = updatedActionStateList)
                is PhaseState.Turn -> latestPhase.copy(actionStateList = updatedActionStateList)
                is PhaseState.River -> latestPhase.copy(actionStateList = updatedActionStateList)
            }
        }.toMutableList()
        // プレイヤーのスタック更新
        val updatedPlayers: List<GamePlayerState> = getNextPlayerStack.invoke(
            latestGameState = latestGameState,
            action = action
        )
        // アクションしていない人がのこっているか？
        val isActionRequired = isActionRequired.invoke(
            playerOrder = latestGameState.playerOrder,
            actionStateList = updatedActionStateList
        )
        return if (isActionRequired) {
            // まだアクションできる
            // プレイヤーのスタックをTableStateに反映
            latestGameState.copy(
                version = latestGameState.version + 1L,
                players = updatedPlayers,
                phaseStateList = updatedPhaseStateList
            )
        } else {
            // もし全員のベットが揃った場合、ポッド更新してフェーズを進める
            // プレイヤーごとの、まだポッドに入っていないベット額
            val pendingBetPerPlayer: Map<PlayerId, Double> = getPendingBetPerPlayer.invoke(
                playerOrder = latestGameState.playerOrder,
                actionStateList = latestPhase.actionStateList
            )
            // ベット状況をポッドに反映
            val updatedPodStateList: List<PodState> = getPodStateList.invoke(
                podStateList = latestGameState.podStateList,
                pendingBetPerPlayer = pendingBetPerPlayer
            )
            // フェーズを進める
            val nextPhase = getNextPhase.invoke(
                playerOrder = latestGameState.playerOrder,
                phaseStateList = updatedPhaseStateList
            )
            updatedPhaseStateList += nextPhase
            // TableState更新
            latestGameState.copy(
                version = latestGameState.version + 1L,
                players = updatedPlayers,
                phaseStateList = updatedPhaseStateList,
                podStateList = updatedPodStateList,
            )
        }
    }
}