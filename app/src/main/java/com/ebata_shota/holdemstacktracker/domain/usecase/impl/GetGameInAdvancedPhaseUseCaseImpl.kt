package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.usecase.GetGameInAdvancedPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotStateListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGameInAdvancedPhaseUseCaseImpl
@Inject
constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPotStateList: GetPotStateListUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getPlayerLastActions: GetPlayerLastActionsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetGameInAdvancedPhaseUseCase {

    /**
     * ポッドに反映して次のフェーズにする
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): Game = withContext(dispatcher) {
        val lastPhase: BetPhase = getLastPhaseAsBetPhase.invoke(currentGame.phaseList)
        // まずはActionを追加
        val actionList = lastPhase.actionStateList

        // PhaseListの最後の要素を置き換える
        val currentPhase: MutableList<Phase> = currentGame.phaseList.toMutableList()
        // プレイヤーごとの、まだポットに入っていないベット額
        val pendingBetPerPlayer: Map<PlayerId, Int> = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = actionList
        )
        // プレイヤーそれぞれの最後のAction
        val lastActions: Map<PlayerId, BetPhaseAction?> = getPlayerLastActions.invoke(playerOrder, currentGame.phaseList)
        // 降りてないプレイヤー
        val activePlayers: List<PlayerId> = lastActions.filter { (_, lastAction) ->
            lastAction !is BetPhaseAction.FoldSkip && lastAction !is BetPhaseAction.Fold
        }.map { it.key }
        // ベット状況をポットに反映
        val updatedPotList: List<Pot> = getPotStateList.invoke(
            potList = currentGame.potList,
            pendingBetPerPlayer = pendingBetPerPlayer,
            activePlayerIds = activePlayers
        )
        // フェーズを進める
        val nextPhase = getNextPhase.invoke(
            playerOrder = playerOrder,
            phaseList = currentPhase
        )
        val updatedPhaseList = currentPhase + nextPhase
        // TableState更新
        return@withContext currentGame.copy(
            version = currentGame.version + 1L,
            potList = updatedPotList,
            phaseList = updatedPhaseList,
        )
    }
}