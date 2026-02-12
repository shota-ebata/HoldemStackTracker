package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.MovePosition
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class MovePositionUseCaseImplTest {

    private lateinit var useCase: MovePositionUseCaseImpl
    private val tableRepository: TableRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = MovePositionUseCaseImpl(tableRepository)
    }

    private fun createTestTable(
        playerOrder: List<PlayerId>,
        basePlayers: List<PlayerBase>
    ): Table {
        return Table(
            id = TableId("table1"),
            version = 1L,
            hostAppVersionCode = 1,
            hostPlayerId = PlayerId("host"),
            potManagerPlayerId = PlayerId("host"),
            rule = Rule.RingGame(sbSize = 1, bbSize = 2, defaultStack = 100),
            basePlayers = basePlayers,
            waitPlayerIds = emptyMap(),
            playerOrder = playerOrder,
            banPlayerIds = emptyList(),
            btnPlayerId = playerOrder.first(),
            tableStatus = TableStatus.PLAYING,
            currentGameId = null,
            startTime = Instant.now(),
            tableCreateTime = Instant.now(),
            updateTime = Instant.now()
        )
    }

    @Test
    fun invoke_withPrev_movesPlayerToPreviousPosition() = runTest {
        // Arrange
        val p1 = PlayerId("player1")
        val p2 = PlayerId("player2")
        val p3 = PlayerId("player3")
        val playerOrder = listOf(p1, p2, p3)
        val basePlayers = playerOrder.map { PlayerBase(it, it.value, 100, isSeated = true, isConnected = true, null) }
        val table = createTestTable(playerOrder, basePlayers)
        val targetPlayerId = p2
        val expectedNewOrder = listOf(p2, p1, p3)

        // Act
        useCase.invoke(targetPlayerId, table, MovePosition.PREV)

        // Assert
        coVerify {
            tableRepository.updatePlayerOrder(
                tableId = table.id,
                playerOrder = expectedNewOrder
            )
        }
    }

    @Test
    fun invoke_withNext_movesPlayerToNextPosition() = runTest {
        // Arrange
        val p1 = PlayerId("player1")
        val p2 = PlayerId("player2")
        val p3 = PlayerId("player3")
        val playerOrder = listOf(p1, p2, p3)
        val basePlayers = playerOrder.map { PlayerBase(it, it.value, 100, isSeated = true, isConnected = true, null) }
        val table = createTestTable(playerOrder, basePlayers)
        val targetPlayerId = p2
        val expectedNewOrder = listOf(p1, p3, p2)

        // Act
        useCase.invoke(targetPlayerId, table, MovePosition.NEXT)

        // Assert
        coVerify {
            tableRepository.updatePlayerOrder(
                tableId = table.id,
                playerOrder = expectedNewOrder
            )
        }
    }
    
    @Test
    fun invoke_withPrevForFirstPlayer_movesToLastPosition() = runTest {
        // Arrange
        val p1 = PlayerId("player1")
        val p2 = PlayerId("player2")
        val p3 = PlayerId("player3")
        val playerOrder = listOf(p1, p2, p3)
        val basePlayers = playerOrder.map { PlayerBase(it, it.value, 100, isSeated = true, isConnected = true, null) }
        val table = createTestTable(playerOrder, basePlayers)
        val targetPlayerId = p1
        val expectedNewOrder = listOf(p2, p3, p1)

        // Act
        useCase.invoke(targetPlayerId, table, MovePosition.PREV)

        // Assert
        coVerify {
            tableRepository.updatePlayerOrder(
                tableId = table.id,
                playerOrder = expectedNewOrder
            )
        }
    }

    @Test
    fun invoke_withNextForLastPlayer_movesToFirstPosition() = runTest {
        // Arrange
        val p1 = PlayerId("player1")
        val p2 = PlayerId("player2")
        val p3 = PlayerId("player3")
        val playerOrder = listOf(p1, p2, p3)
        val basePlayers = playerOrder.map { PlayerBase(it, it.value, 100, isSeated = true, isConnected = true, null) }
        val table = createTestTable(playerOrder, basePlayers)
        val targetPlayerId = p3
        val expectedNewOrder = listOf(p3, p1, p2)

        // Act
        useCase.invoke(targetPlayerId, table, MovePosition.NEXT)

        // Assert
        coVerify {
            tableRepository.updatePlayerOrder(
                tableId = table.id,
                playerOrder = expectedNewOrder
            )
        }
    }
    
    @Test
    fun invoke_withSeatedAndNotSeatedPlayers() = runTest {
        // Arrange
        val p1 = PlayerId("player1") // seated
        val p2 = PlayerId("player2") // not seated
        val p3 = PlayerId("player3") // seated
        val p4 = PlayerId("player4") // seated
        val playerOrder = listOf(p1, p2, p3, p4)
        val basePlayers = listOf(
            PlayerBase(p1, p1.value, 100, isSeated = true, isConnected = true, null),
            PlayerBase(p2, p2.value, 100, isSeated = false, isConnected = true, null),
            PlayerBase(p3, p3.value, 100, isSeated = true, isConnected = true, null),
            PlayerBase(p4, p4.value, 100, isSeated = true, isConnected = true, null),
        )
        val table = createTestTable(playerOrder, basePlayers)
        val targetPlayerId = p1
        // playerOrderWithoutLeaved is [p1, p3, p4]. next player of p1 is p3.
        val expectedNewOrder = listOf(p2, p3, p1, p4)

        // Act
        useCase.invoke(targetPlayerId, table, MovePosition.NEXT)

        // Assert
        coVerify {
            tableRepository.updatePlayerOrder(
                tableId = table.id,
                playerOrder = expectedNewOrder
            )
        }
    }
}