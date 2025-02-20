package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class CreateNewGameUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
    private val gameRepository: GameRepository,
    private val randomIdRepository: RandomIdRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : CreateNewGameUseCase {

    override suspend fun invoke(table: Table, fromPreFlop: Boolean) = withContext(dispatcher) {
        val updateTime = Instant.now()
        val gameId = GameId(randomIdRepository.generateRandomId())
        val copiedTable = table.copy(
            tableStatus = TableStatus.PLAYING,
            currentGameId = gameId,
            startTime = updateTime,
            updateTime = updateTime,
            version = table.version + 1
        )
        val newGame = Game(
            gameId = gameId,
            tableId = table.id, // MEMO: RealtimeDBには保存はされない
            version = 0,
            appVersion = BuildConfig.VERSION_CODE.toLong(),
            btnPlayerId = table.btnPlayerId,
            players = table.playerOrderWithoutLeaved.mapNotNull { playerId ->
                val player = table.basePlayers.find { it.id == playerId }
                player?.let {
                    GamePlayer(
                        id = player.id,
                        stack = player.stack,
                    )
                }
            },
            potList = emptyList(),
            phaseList = listOfNotNull(
                Phase.Standby(
                    phaseId = PhaseId(randomIdRepository.generateRandomId())
                ),
                if (fromPreFlop) Phase.PreFlop(
                    phaseId = PhaseId(randomIdRepository.generateRandomId()),
                    actionStateList = emptyList()
                ) else null
            ),
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