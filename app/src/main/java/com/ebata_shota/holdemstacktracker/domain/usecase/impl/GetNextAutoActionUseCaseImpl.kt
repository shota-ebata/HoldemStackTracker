package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.exception.getLatestBetPhase
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextAutoActionUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase,
    private val getMaxBetSize: GetMaxBetSizeUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val randomIdRepository: RandomIdRepository,
    @CoroutineDispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : GetNextAutoActionUseCase {

    /**
     * 現状のGame状態に対して発生しうる
     * AutoActionを取得する
     */
    override suspend fun invoke(
        actionPlayerId: PlayerId,
        rule: Rule,
        leavedPlayerIds: List<PlayerId>,
        game: Game,
    ): BetPhaseAction? = withContext(dispatcher) {
        return@withContext when (val latestPhase = game.phaseList.lastOrNull()) {
            is Phase.Standby -> null
            is Phase.PreFlop -> {
                getPreFlopAutoAction(
                    latestPhase = latestPhase,
                    rule = rule,
                    playerOrder = game.playerOrder,
                    actionPlayerId = actionPlayerId,
                    game = game,
                    leavedPlayerIds = leavedPlayerIds,
                )
            }

            is Phase.Flop, is Phase.Turn, is Phase.River -> {
                getAutoAction(
                    game = game,
                    actionPlayerId = actionPlayerId,
                    playerOrder = game.playerOrder,
                    leavedPlayerIds = leavedPlayerIds,
                )
            }

            is Phase.PotSettlement, is Phase.End, null -> null
        }
    }

    private suspend fun getPreFlopAutoAction(
        latestPhase: Phase.PreFlop,
        rule: Rule,
        playerOrder: List<PlayerId>,
        actionPlayerId: PlayerId,
        game: Game,
        leavedPlayerIds: List<PlayerId>,
    ): BetPhaseAction? {
        return when (rule) {
            is Rule.RingGame -> {
                getPreFlopRingGameAutoAction(
                    actionList = latestPhase.actionStateList,
                    playerId = actionPlayerId,
                    rule = rule,
                    game = game,
                    playerOrder = playerOrder,
                    leavedPlayerIds = leavedPlayerIds,
                )
            }
        }
    }

    private suspend fun getPreFlopRingGameAutoAction(
        actionList: List<BetPhaseAction>,
        playerId: PlayerId,
        rule: Rule.RingGame,
        game: Game,
        playerOrder: List<PlayerId>,
        leavedPlayerIds: List<PlayerId>,
    ): BetPhaseAction? {
        // Betアクションのみ絞り込み
        val betActionList = actionList.filterIsInstance<BetPhaseAction.BetAction>()
        return when (betActionList.size) {
            // まだBetアクションがない場合はSBをBlindするように、調整
            0 -> getAutoSB(game, playerId, rule)
            // すでにBetアクションがある場合はBBをBlindするように
            1 -> getAutoBB(game, playerId, rule)
            // 他の状態の場合で、オートアクションがあれば
            else -> {
                getAutoAction(
                    game = game,
                    actionPlayerId = playerId,
                    playerOrder = playerOrder,
                    leavedPlayerIds = leavedPlayerIds,
                )
            }
        }
    }

    private fun getAutoSB(
        game: Game,
        playerId: PlayerId,
        rule: Rule.RingGame
    ): BetPhaseAction {
        val stack = game.players.find { it.id == playerId }!!.stack
        return when {
            // bbに足りている場合のみ、SBを支払う
            stack >= rule.bbSize -> {
                BetPhaseAction.Blind(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = playerId,
                    betSize = rule.sbSize
                )
            }
            // 足りない場合は強制Fold
            else -> {
                BetPhaseAction.Fold(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = playerId,
                )
            }
        }
    }

    private fun getAutoBB(
        game: Game,
        playerId: PlayerId,
        rule: Rule.RingGame
    ): BetPhaseAction {
        val stack = game.players.find { it.id == playerId }!!.stack
        return when {
            // bbを超えている場合は、BBを支払う
            stack > rule.bbSize -> {
                BetPhaseAction.Blind(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = playerId,
                    betSize = rule.bbSize,
                )
            }
            // bbと一致している場合は、BBのサイズでAllIn
            stack == rule.bbSize -> {
                BetPhaseAction.AllIn(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = playerId,
                    betSize = stack
                )
            }
            // 足りない場合は強制Fold
            else -> {
                BetPhaseAction.Fold(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = playerId
                )
            }
        }
    }

    private suspend fun getAutoAction(
        game: Game,
        actionPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        leavedPlayerIds: List<PlayerId>,
    ): BetPhaseAction? {
        val lastActions: Map<PlayerId, BetPhaseAction?> =
            getPlayerLastActions.invoke(
                playerOrder = playerOrder,
                phaseList = game.phaseList
            )
        // プレイヤーが最後にやったアクションに応じて
        // オートアクションがある場合は返す
        return when (lastActions[actionPlayerId]) {
            is BetPhaseAction.Fold, is BetPhaseAction.FoldSkip -> {
                BetPhaseAction.FoldSkip(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = actionPlayerId
                )
            }

            is BetPhaseAction.AllIn, is BetPhaseAction.AllInSkip -> {
                BetPhaseAction.AllInSkip(
                    actionId = ActionId(randomIdRepository.generateRandomId()),
                    playerId = actionPlayerId
                )
            }

            is BetPhaseAction.Blind,
            is BetPhaseAction.Check,
            is BetPhaseAction.Call,
            is BetPhaseAction.Bet,
            is BetPhaseAction.Raise,
            null,
                -> {
                // TODO: 複雑なので整理整頓したい
                if (leavedPlayerIds.any { it == actionPlayerId }) {
                    // 明確に離席中のプレイヤーの場合は、Check Fold をオートアクションにする
                    val betPhase = game.phaseList.getLatestBetPhase()
                        ?: throw IllegalStateException("betPhase以外ここに来ない想定")
                    val isEnableCheckAction =
                        isEnableCheckAction(betPhase, playerOrder, actionPlayerId)
                    if (isEnableCheckAction) {
                        // check可能ならcheck
                        BetPhaseAction.Check(
                            actionId = ActionId(randomIdRepository.generateRandomId()),
                            playerId = actionPlayerId,
                        )
                    } else {
                        // checkできないならFold
                        BetPhaseAction.Fold(
                            actionId = ActionId(randomIdRepository.generateRandomId()),
                            playerId = actionPlayerId,
                        )
                    }
                } else {
                    null
                }
            }
        }
    }

    // TODO: UseCase化してGameContentUiStateMapperでも利用できるようにしたい
    private suspend fun isEnableCheckAction(
        betPhase: Phase.BetPhase,
        playerOrder: List<PlayerId>,
        actionPlayerId: PlayerId,
    ): Boolean {
        val maxBetSize = getMaxBetSize.invoke(actionStateList = betPhase.actionStateList)
        val pendingBetPerPlayer = getPendingBetPerPlayer.invoke(
            playerOrder = playerOrder,
            actionStateList = betPhase.actionStateList
        )
        val pendingBetSize = pendingBetPerPlayer[actionPlayerId] ?: 0
        return maxBetSize == pendingBetSize
    }
}