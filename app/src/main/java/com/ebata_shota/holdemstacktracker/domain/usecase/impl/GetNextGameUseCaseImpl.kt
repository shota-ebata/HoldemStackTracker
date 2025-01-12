package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextGameUseCaseImpl
@Inject
constructor(
    private val isActionRequired: IsActionRequiredUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPotStateList: GetPotStateListUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getNextPlayerStack: GetNextPlayerStackUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetNextGameUseCase {

    override suspend fun invoke(
        latestGame: Game,
        action: Action,
        playerOrder: List<PlayerId>
    ): Game = withContext(dispatcher) {
        when (action) {
            is BetPhaseAction -> {
                return@withContext getNextGameFromBetPhaseAction(
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
        val latestPhase: BetPhase = getLastPhaseAsBetPhase.invoke(latestGame.phaseList)
        // まずはActionを追加
        val updatedActionStateList = latestPhase.actionStateList + action
        val latestPhaseList: List<Phase> = latestGame.phaseList
        // PhaseListの最後の要素を置き換える
        val updatedPhaseList: MutableList<Phase> = latestPhaseList.mapAtIndex(latestPhaseList.lastIndex) {
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
                phaseList = updatedPhaseList
            )
        } else {
            // もし全員のベットが揃った場合、ポット更新してフェーズを進める
            getNextGame(
                playerOrder = playerOrder,
                latestGame = latestGame,
                updatedPlayers = updatedPlayers,
                updatedPhaseList = updatedPhaseList,
                updatedActionStateList = updatedActionStateList,
            )
        }
    }

    private suspend fun getNextGame(
        playerOrder: List<PlayerId>,
        latestGame: Game,
        updatedPlayers: Set<GamePlayer>,
        updatedPhaseList: MutableList<Phase>,
        updatedActionStateList: List<BetPhaseAction>,
    ): Game {
        // プレイヤーごとの、まだポットに入っていないベット額
        val pendingBetPerPlayer: Map<PlayerId, Int> = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = updatedActionStateList
        )
        // ベット状況をポットに反映
        val updatedPotList: List<Pot> = getPotStateList.invoke(
            potList = latestGame.potList,
            pendingBetPerPlayer = pendingBetPerPlayer
        )
        // フェーズを進める
        val nextPhase = getNextPhase.invoke(
            playerOrder = playerOrder,
            phaseList = updatedPhaseList
        )
        updatedPhaseList += nextPhase
        // TableState更新
        return latestGame.copy(
            version = latestGame.version + 1L,
            players = updatedPlayers,
            phaseList = updatedPhaseList,
            potList = updatedPotList,
        )
    }
}