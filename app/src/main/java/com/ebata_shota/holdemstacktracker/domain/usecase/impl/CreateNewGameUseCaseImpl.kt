package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import javax.inject.Inject

class CreateNewGameUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository
) : CreateNewGameUseCase {
    override suspend fun invoke(newTable: Table, newGame: Game) {
        tableRepository.sendTable(
            newTable = newTable
        )
        gameRepository.sendGame(
            tableId = newTable.id,
            newGame = newGame
        )
    }
}