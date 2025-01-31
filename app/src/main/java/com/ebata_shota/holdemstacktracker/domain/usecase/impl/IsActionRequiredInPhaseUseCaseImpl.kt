package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionablePlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredInPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IsActionRequiredInPhaseUseCaseImpl
@Inject
constructor(
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val getNotFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
    private val getActionablePlayerIds: GetActionablePlayerIdsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : IsActionRequiredInPhaseUseCase {

    /**
     * アクションが必要な人がいるか？
     * (より正確に言えば、フェーズとしてアクションが必要な状態か？)
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>,
    ): Boolean = withContext(dispatcher) {
        // 降りてないプレイヤー
        val notFoldPlayerIds: List<PlayerId> = getNotFoldPlayerIds.invoke(
            playerOrder = playerOrder,
            phaseList = phaseList
        )

        if (notFoldPlayerIds.size <= 1) {
            // 降りていないプレイヤ＝が1人以下になった場合
            // もうアクションは不要
            return@withContext false
        }

        // アクション権を持つプレイヤーID一覧
        val actionablePlayerIds = getActionablePlayerIds.invoke(
            playerOrder = playerOrder,
            phaseList = phaseList,
        )

        if (actionablePlayerIds.size <= 1) {
            // アクション権を持つプレイヤーが1人以下なら
            val lastBetPhase = phaseList.last() as Phase.BetPhase

            // 全員の最後のアクションを一つづつ取得
            val playerOrderSize = playerOrder.size
            val lastActionList = lastBetPhase.actionStateList.takeLast(playerOrderSize)

            // アクションが必要な人がいるか？
            return@withContext isExistsRequiredActionPlayer(
                lastActionList = lastActionList,
                lastBetPhase = lastBetPhase,
                playerOrder = playerOrder
            )
        }

        val lastBetPhase = phaseList.last() as Phase.BetPhase
        val playerOrderSize = playerOrder.size
        val lastActionList = lastBetPhase.actionStateList.takeLast(playerOrderSize)
        // Blindを除いたアクション一覧
        val lastActionListWithoutBlind = lastActionList
            .filterNot { it is BetPhaseAction.Blind } // Blindは除く
        // アクション数がプレイヤー人数より少ないなら、アクションが必要な人がいる
        if (lastActionListWithoutBlind.size < playerOrderSize) {
            return@withContext true
        }

        return@withContext isExistsRequiredActionPlayer(
            lastActionList = lastActionList,
            lastBetPhase = lastBetPhase,
            playerOrder = playerOrder
        )
    }

    /**
     * アクションが必要なプレイヤーがいるかどうか？を返す
     */
    private suspend fun isExistsRequiredActionPlayer(
        lastActionList: List<BetPhaseAction>,
        lastBetPhase: Phase.BetPhase,
        playerOrder: List<PlayerId>,
    ): Boolean = lastActionList.any { action ->
        // コールベットサイズ
        val callBetSize: Int = getMaxBetSize.invoke(lastActionList)
        when (action) {
            // オールインはこれ以上アクションできない判断
            is BetPhaseAction.AllIn -> false
            // AllIn以外のベットアクションでコールに必要なベットサイズと異なる場合は、アクションが必要
            is BetPhaseAction.BetAction -> {
                action.betSize != callBetSize
            }
            // その人がすでに賭けているベット額がコール額と一致していないなら、アクションが必要
            is BetPhaseAction.Check -> {
                val pendingBet = getPendingBetSize.invoke(
                    actionList = lastBetPhase.actionStateList,
                    playerOrder = playerOrder,
                    playerId = action.playerId
                )
                pendingBet != callBetSize
            }

            is BetPhaseAction.Fold -> false
            is BetPhaseAction.FoldSkip -> false
            is BetPhaseAction.AllInSkip -> false
        }
    }
}