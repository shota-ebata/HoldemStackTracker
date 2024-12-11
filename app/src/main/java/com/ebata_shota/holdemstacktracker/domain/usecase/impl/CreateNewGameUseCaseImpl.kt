package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import java.time.Instant
import javax.inject.Inject

class CreateNewGameUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository
) : CreateNewGameUseCase {
    override suspend fun invoke(table: Table) {
        val updateTime = Instant.now()
        val copiedTable = table.copy(
            tableStatus = TableStatus.PLAYING,
            startTime = updateTime,
            updateTime = updateTime,
            version = table.version + 1
        )
        val newGame = Game(
            version = 0,
            appVersion = BuildConfig.VERSION_CODE.toLong(),
            players = table.playerOrder.mapNotNull { playerId ->
                val player = table.basePlayers.find { it.id == playerId }
                player?.let {
                    GamePlayerState(
                        id = player.id,
                        stack = player.stack,
                        isLeaved = false
                    )
                }
            },
            podList = emptyList(),
            phaseStateList = listOf(PhaseState.Standby),
            updateTime = updateTime
        )
        tableRepository.sendTable(
            table = copiedTable
        )
        gameRepository.sendGame(
            tableId = copiedTable.id,
            newGame = newGame
        )
    }
}