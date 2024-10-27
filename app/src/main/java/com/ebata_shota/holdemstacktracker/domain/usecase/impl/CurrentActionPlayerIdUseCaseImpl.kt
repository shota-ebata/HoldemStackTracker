package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.usecase.CurrentActionPlayerIdUseCase
import javax.inject.Inject


class CurrentActionPlayerIdUseCaseImpl
@Inject
constructor() : CurrentActionPlayerIdUseCase {
    override fun getCurrentActionPlayerId(tableState: TableState): Long? {
        return tableState.phaseStateList.lastOrNull()?.let { phaseState ->
            val lastActionPlayerId: Long? = when (phaseState) {
                is PhaseState.BetPhase -> phaseState.actionStateList.lastOrNull()?.playerId
                else -> return@let null
            }
            if (lastActionPlayerId != null) {
                // 最後にアクションした人の次の人が
                // 現在のアクションを行うプレイヤー
                getCurrentActionPlayerId(tableState.playerOrder, lastActionPlayerId)
            } else {
                // lastActionPlayerIdがnull
                // つまり、だれもアクションしていないフェーズの開始時
                // BTNの次の人からアクションを開始する
                getCurrentActionPlayerId(tableState.playerOrder, tableState.btnPlayerId)
            }
        }
    }

    private fun getCurrentActionPlayerId(playerOrder: List<Long>, basePlayerId: Long?): Long {
        val playerOrderLastIndex = playerOrder.lastIndex
        if (playerOrderLastIndex == -1) {
            throw IllegalStateException("playerOrderが空")
        }
        val lastActionPlayerIndex = playerOrder.indexOf(basePlayerId).apply {
            if (this@apply == -1) {
                throw IllegalStateException("basePlayerId=$basePlayerId がplayerOrderに存在しない")
            }
        }
        // basePlayerIdの次のプレイヤーのIndexを返す
        return if (playerOrderLastIndex == lastActionPlayerIndex) {
            // 最後なら0
            playerOrder[0]
        } else {
            playerOrder[lastActionPlayerIndex + 1]
        }
    }
}