package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddBetPhaseActionInToGameUseCaseImpl
@Inject
constructor(
    private val isActionRequired: IsActionRequiredUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getNextPlayerStack: GetNextPlayerStackUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : AddBetPhaseActionInToGameUseCase {

    override suspend fun invoke(
        currentGame: Game,
        betPhaseAction: BetPhaseAction,
        playerOrder: List<PlayerId>,
    ): Game = withContext(dispatcher) {
        // BetPhaseでしかActionはできないので
        val currentPhase: BetPhase = getLastPhaseAsBetPhase.invoke(currentGame.phaseList)
        // まずはActionを追加
        val updatedActionList = currentPhase.actionStateList + betPhaseAction
        val latestPhaseList: List<Phase> = currentGame.phaseList
        // アクションしていない人がのこっているか？
        val isActionRequired = isActionRequired.invoke(
            playerOrder = playerOrder,
            actionStateList = updatedActionList
        )

        // プレイヤーのスタック更新
        val updatedPlayers: Set<GamePlayer> = getNextPlayerStack.invoke(
            latestGame = currentGame,
            action = betPhaseAction,
            playerOrder = playerOrder
        )

        // PhaseListの最後の要素を置き換える
        val updatedPhase: BetPhase = when (currentPhase) {
            is Phase.PreFlop -> currentPhase.copy(
                actionStateList = updatedActionList,
                isClosed = !isActionRequired,
            )

            is Phase.Flop -> currentPhase.copy(
                actionStateList = updatedActionList,
                isClosed = !isActionRequired,
            )

            is Phase.Turn -> currentPhase.copy(
                actionStateList = updatedActionList,
                isClosed = !isActionRequired,
            )

            is Phase.River -> currentPhase.copy(
                actionStateList = updatedActionList,
                isClosed = !isActionRequired,
            )
        }
        val updatedPhaseList: MutableList<Phase> =
            latestPhaseList.mapAtIndex(latestPhaseList.lastIndex) {
                // Phaseに反映
                updatedPhase
            }.toMutableList()

        return@withContext currentGame.copy(
            version = currentGame.version + 1L,
            players = updatedPlayers,
            phaseList = updatedPhaseList
        )
    }
}