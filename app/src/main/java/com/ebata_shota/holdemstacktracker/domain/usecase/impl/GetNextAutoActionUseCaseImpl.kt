package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import javax.inject.Inject

class GetNextAutoActionUseCaseImpl
@Inject
constructor(
    private val getPlayerLastActions: GetPlayerLastActionsUseCase
) : GetNextAutoActionUseCase {

    override suspend fun invoke(
        playerId: PlayerId,
        table: Table,
        game: Game
    ): BetPhaseAction? {
        return when (val latestPhase = game.phaseList.lastOrNull()) {
            Phase.Standby -> null
            is Phase.PreFlop -> {
                getPreFlopAutoAction(
                    latestPhase = latestPhase,
                    table = table,
                    playerId = playerId,
                    game = game
                )
            }

            is Phase.Flop,
            is Phase.Turn,
            is Phase.River -> {
                getAutoAction(
                    game = game,
                    playerId = playerId,
                    table = table
                )
            }

            Phase.ShowDown,
            Phase.AllInOpen,
            Phase.PotSettlement,
            Phase.End,
            null -> null
        }
    }

    private suspend fun getPreFlopAutoAction(
        latestPhase: Phase.PreFlop,
        table: Table,
        playerId: PlayerId,
        game: Game
    ): BetPhaseAction? {
        return when (val rule = table.rule) {
            is Rule.RingGame -> {
                getPreFlopRingGameAutoAction(
                    actionList = latestPhase.actionStateList,
                    playerId = playerId,
                    rule = rule,
                    game = game,
                    table = table
                )
            }
        }
    }

    private suspend fun getPreFlopRingGameAutoAction(
        actionList: List<BetPhaseAction>,
        playerId: PlayerId,
        rule: Rule.RingGame,
        game: Game,
        table: Table
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
                    playerId = playerId,
                    table = table
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
                    playerId = playerId,
                    betSize = rule.sbSize
                )
            }
            // 足りない場合は強制Fold
            else -> {
                BetPhaseAction.Fold(playerId)
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
            stack >= rule.bbSize -> {
                BetPhaseAction.Blind(
                    playerId = playerId,
                    betSize = rule.bbSize
                )
            }
            // bbと一致している場合は、BBのサイズでAllIn
            stack == rule.bbSize -> {
                BetPhaseAction.AllIn(
                    playerId = playerId,
                    betSize = stack
                )
            }
            // 足りない場合は強制Fold
            else -> {
                BetPhaseAction.Fold(playerId)
            }
        }
    }

    private suspend fun getAutoAction(
        game: Game,
        playerId: PlayerId,
        table: Table
    ): BetPhaseAction? {
        val lastActions: Map<PlayerId, BetPhaseAction?> =
            getPlayerLastActions.invoke(
                playerOrder = table.playerOrder,
                phaseList = game.phaseList
            )
        // プレイヤーが最後にやったアクションに応じて
        // オートアクションがある場合は返す
        return when (lastActions[playerId]) {
            is BetPhaseAction.Fold, is BetPhaseAction.FoldSkip -> {
                BetPhaseAction.FoldSkip(playerId)
            }

            is BetPhaseAction.AllIn, is BetPhaseAction.AllInSkip -> {
                BetPhaseAction.AllInSkip(playerId)
            }

            is BetPhaseAction.Blind,
            is BetPhaseAction.Check,
            is BetPhaseAction.Call,
            is BetPhaseAction.Bet,
            is BetPhaseAction.Raise,
            null -> {
                val gamePlayer = game.players.find { it.id == playerId }
                if (gamePlayer?.isLeaved == true) {
                    // 離席の場合は強制Fold
                    BetPhaseAction.Fold(playerId)
                } else {
                    null
                }
            }
        }
    }
}