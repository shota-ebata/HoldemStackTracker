package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextBtnPlayerIdUseCase
import javax.inject.Inject

class GetNextBtnPlayerIdUseCaseImpl
@Inject
constructor() : GetNextBtnPlayerIdUseCase {
    override fun invoke(
        table: Table,
        game: Game,
    ): PlayerId? {
        val lastBtnPlayerId = game.btnPlayerId
        val lastBtnPlayerIndex =
            table.playerOrderWithoutLeaved.indexOfFirstOrNull { it == lastBtnPlayerId }
        val nextIndex = if (lastBtnPlayerIndex != null) {
            (lastBtnPlayerIndex + 1) % table.playerOrderWithoutLeaved.size
        } else {
            null
        }
        return nextIndex?.let { table.playerOrderWithoutLeaved[nextIndex] }
    }
}