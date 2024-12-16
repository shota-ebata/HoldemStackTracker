package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pod
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPodStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import javax.inject.Inject

class GetNextGameUseCaseImpl
@Inject
constructor(
    private val isActionRequired: IsActionRequiredUseCase,
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPodStateList: GetPodStateListUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getNextPlayerStack: GetNextPlayerStackUseCase,
) : GetNextGameUseCase {

    override suspend fun invoke(
        latestGame: Game,
        action: Action,
        playerOrder: List<PlayerId>
    ): Game {
        when (action) {
            is BetPhaseAction -> {
                return getNextGameFromBetPhaseAction(
                    latestGame = latestGame,
                    action = action,
                    playerOrder = playerOrder
                )
            }
        }
    }

    private suspend fun getNextGameFromBetPhaseAction(
        latestGame: Game,
        action: BetPhaseAction,
        playerOrder: List<PlayerId>
    ): Game {
        // BetPhaseでしかActionはできないので
        val latestPhase: BetPhase = getLatestBetPhase.invoke(latestGame)
        // まずはActionを追加
        val updatedActionStateList = latestPhase.actionStateList + action
        val latestPhaseList: List<Phase> = latestGame.phaseList
        // PhaseListの最後の要素を置き換える
        val updatedPhaseLists: MutableList<Phase> = latestPhaseList.mapAtIndex(latestPhaseList.lastIndex) {
            // Phaseに反映
            when (latestPhase) {
                is Phase.PreFlop -> latestPhase.copy(actionStateList = updatedActionStateList)
                is Phase.Flop -> latestPhase.copy(actionStateList = updatedActionStateList)
                is Phase.Turn -> latestPhase.copy(actionStateList = updatedActionStateList)
                is Phase.River -> latestPhase.copy(actionStateList = updatedActionStateList)
            }
        }.toMutableList()
        // プレイヤーのスタック更新
        val updatedPlayers: Set<GamePlayer> = getNextPlayerStack.invoke(
            latestGame = latestGame,
            action = action,
            playerOrder = playerOrder
        )
        // アクションしていない人がのこっているか？
        val isActionRequired = isActionRequired.invoke(
            playerOrder = playerOrder,
            actionStateList = updatedActionStateList
        )
        return if (isActionRequired) {
            // まだアクションできる
            // プレイヤーのスタックをTableStateに反映
            latestGame.copy(
                version = latestGame.version + 1L,
                players = updatedPlayers,
                phaseList = updatedPhaseLists
            )
        } else {
            // もし全員のベットが揃った場合、ポッド更新してフェーズを進める
            // プレイヤーごとの、まだポッドに入っていないベット額
            val pendingBetPerPlayer: Map<PlayerId, Double> = getPendingBetPerPlayer.invoke(
                playerOrder = playerOrder,
                actionStateList = latestPhase.actionStateList
            )
            // ベット状況をポッドに反映
            val updatedPodList: List<Pod> = getPodStateList.invoke(
                podList = latestGame.podList,
                pendingBetPerPlayer = pendingBetPerPlayer
            )
            // フェーズを進める
            val nextPhase = getNextPhase.invoke(
                playerOrder = playerOrder,
                phaseList = updatedPhaseLists
            )
            updatedPhaseLists += nextPhase
            // TableState更新
            latestGame.copy(
                version = latestGame.version + 1L,
                players = updatedPlayers,
                phaseList = updatedPhaseLists,
                podList = updatedPodList,
            )
        }
    }
}