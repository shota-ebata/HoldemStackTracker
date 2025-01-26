package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActivePlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameFromIntervalUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotStateListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextGameFromIntervalUseCaseImpl
@Inject
constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPotStateList: GetPotStateListUseCase,
    private val getNextPhase: GetNextPhaseUseCase,
    private val getActivePlayerIds: GetActivePlayerIdsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetNextGameFromIntervalUseCase {

    /**
     * インターバル状態にGameから
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
        // 降りてないプレイヤー
        val activePlayers: List<PlayerId> = getActivePlayerIds.invoke(
            playerOrder = playerOrder,
            phaseList = currentGame.phaseList
        )
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
            potList = updatedPotList,
            phaseList = updatedPhaseList,
        )
    }

}