package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtFind
import com.ebata_shota.holdemstacktracker.domain.extension.mapAtIndex
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PhaseStatus
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PotAndRemainingBet
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionablePlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredInPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddBetPhaseActionInToGameUseCaseImpl
@Inject
constructor(
    private val isActionRequiredInPhase: IsActionRequiredInPhaseUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getNextPlayerStack: GetNextPlayerStackUseCase,
    private val getNotFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
    private val getRequiredActionPlayerIds: GetActionablePlayerIdsUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getPotStateList: GetPotListUseCase,
    private val randomIdRepository: RandomIdRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : AddBetPhaseActionInToGameUseCase {

    /**
     * BetPhaseアクションをGameに追加する
     */
    override suspend fun invoke(
        currentGame: Game,
        betPhaseAction: BetPhaseAction,
    ): Game = withContext(dispatcher) {
        // BetPhaseでしかActionはできないので
        val currentPhase: BetPhase = getLastPhaseAsBetPhase.invoke(currentGame.phaseList)
        // まずはActionを追加
        val addedActionList = currentPhase.actionStateList + betPhaseAction
        val currentPhaseList: List<Phase> = currentGame.phaseList

        // プレイヤーのスタック更新
        val updatedPlayers: List<GamePlayer> = getNextPlayerStack.invoke(
            latestGame = currentGame,
            action = betPhaseAction,
        )

        // PhaseListの最後の要素を置き換える
        val addedActionPhase: BetPhase = currentPhase.copyWith(actionList = addedActionList)
        // 現在のPhaseの内容を更新する
        val addedActionPhaseList: MutableList<Phase> =
            currentPhaseList.mapAtIndex(currentPhaseList.lastIndex) {
                // Phaseに反映
                addedActionPhase
            }.toMutableList()
        val baseNextGame = currentGame.copy(
            players = updatedPlayers,
        )

        // アクションしていない人がのこっているか？
        val isActionRequired = isActionRequiredInPhase.invoke(
            playerOrder = baseNextGame.playerOrder,
            phaseList = addedActionPhaseList
        )
        if (isActionRequired) {
            // まだアクションが必要なプレイヤーがいる場合
            // アクションを追加するだけに留める
            return@withContext baseNextGame.copy(
                phaseList = addedActionPhaseList
            )
        }

        /**
         * 以降はアクション追加以外の対応
         * ・Foldで即終了、精算フェーズに
         * ・AllInで即終了、AllInCloseに
         * ・Closeして次のフェーズを進めたい
         */

        // もうアクションが不要な場合
        // 次のフェーズに進めたい
        // 降りてないプレイヤーID一覧
        val notFoldPlayerIds = getNotFoldPlayerIds.invoke(
            playerOrder = baseNextGame.playerOrder,
            phaseList = addedActionPhaseList
        )
        if (notFoldPlayerIds.size <= 1) {
            /**
             * Foldで即終了、精算フェーズに
             *
             * 一人を除いて、全員降りている場合
             * ベット状況をポットに反映
             */
            val potAndRemainingBet: PotAndRemainingBet = getUpdatedPotList(
                updatedPlayers = updatedPlayers,
                playerOrder = baseNextGame.playerOrder,
                addedActionList = addedActionList,
                currentGame = currentGame,
                activePlayerIds = notFoldPlayerIds
            )
            return@withContext baseNextGame.copy(
                potList = potAndRemainingBet.potList,
                phaseList = addedActionPhaseList.mapAtIndex(currentPhaseList.lastIndex) {
                    // Phaseに反映
                    addedActionPhase.copyWith(phaseStatus = PhaseStatus.Close)
                }.toMutableList().apply {
                    // PotSettlementフェーズを加える
                    add(
                        Phase.PotSettlement(
                            phaseId = PhaseId(randomIdRepository.generateRandomId())
                        )
                    )
                },
            )
        }

        // 降りていないプレイヤーが複数人いる

        // アクションが必要なプレイヤーの数
        // 正確に言うと降りているわけでもなく、AllInしているわけでもないプレイヤーIDの一覧を取得
        val requiredActionPlayerIds = getRequiredActionPlayerIds.invoke(
            playerOrder = baseNextGame.playerOrder,
            phaseList = addedActionPhaseList
        )
        if (requiredActionPlayerIds.size < 2) {
            /**
             * ここまでの条件が
             * すべてfalseという前提の時
             * AllInで即終了、AllInCloseに
             *
             * 降りていないプレイヤーが複数人いて
             * 降りてない、AllInしているわででもないプレイヤーが2人未満なら
             * AllIn でフェーズを終えているということなので
             * AllInCloseとする
             * フェーズは変えないのでポットへの反映はまだしない
             */
            return@withContext baseNextGame.copy(
                phaseList = addedActionPhaseList.mapAtIndex(currentPhaseList.lastIndex) {
                    // Phaseに反映
                    addedActionPhase.copyWith(phaseStatus = PhaseStatus.AllInClose)
                },
            )
        }

        /**
         * Closeして次のフェーズを進めたい
         *
         * もうアクションが不要な場合かつ
         * 降りていないプレイヤーが2人以上の場合で
         * 降りてない、AllInしているわででもないプレイヤーが2人以上なら
         * つまり、このフェーズを終えて、次のフェーズに進みたい
         * アクションを追加するだけに留める
         */
        return@withContext baseNextGame.copy(
            phaseList = addedActionPhaseList.mapAtIndex(currentPhaseList.lastIndex) {
                // Phaseに反映
                addedActionPhase.copyWith(phaseStatus = PhaseStatus.Close)
            },
        )
    }

    private suspend fun getUpdatedPotList(
        updatedPlayers: List<GamePlayer>,
        playerOrder: List<PlayerId>,
        addedActionList: List<BetPhaseAction>,
        currentGame: Game,
        activePlayerIds: List<PlayerId>,
    ): PotAndRemainingBet {
        // プレイヤーごとの、まだポットに入っていないベット額
        val pendingBetPerPlayer: Map<PlayerId, Int> = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = addedActionList
        )
        // ベット状況をポットに反映
        return getPotStateList.invoke(
            updatedPlayers = updatedPlayers,
            potList = currentGame.potList,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayer,
            activePlayerIds = activePlayerIds
        )
    }
}