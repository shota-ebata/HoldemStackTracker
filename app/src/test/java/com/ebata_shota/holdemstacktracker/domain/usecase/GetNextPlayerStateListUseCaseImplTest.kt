package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStateListUseCaseImpl
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextPlayerStateListUseCaseImplTest {
    private lateinit var usecase: GetNextPlayerStateListUseCaseImpl

    private val prefRepository: PrefRepository = mockk()

    @Before
    fun setup() {
        usecase = GetNextPlayerStateListUseCaseImpl(
            prefRepository = prefRepository
        )
    }


    @After
    fun reset() {
        clearMocks(prefRepository)
    }

    private fun executeAndAssert(
        pendingBetPerPlayer: Map<PlayerId, Double> = emptyMap(),
        players: List<PlayerState>,
        action: BetPhaseActionState,
        expected: List<PlayerState>,
    ) {
        runTest {
            // execute
            val actual: List<PlayerState> = usecase.invoke(
                pendingBetPerPlayer = pendingBetPerPlayer,
                players = players,
                action = action
            )
            // assert
            assertEquals(expected, actual)
        }
    }

    @Test
    fun action_Blind() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 900.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Call() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Call(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 900.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Raise() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Raise(actionId = 0L, playerId = PlayerId("0"), betSize = 400.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 600.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Bet() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Bet(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 900.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_AllIn() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.AllIn(actionId = 0L, playerId = PlayerId("0"), betSize = 1000.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 0.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Check() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Fold() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Fold(actionId = 0L, playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Skip() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseActionState.FoldSkip(actionId = 0L, playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 1000.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_Call() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 200.0,
        )
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 900.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 800.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Call(actionId = 0L, playerId = PlayerId("0"), betSize = 200.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 800.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 800.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_AllIn() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("0")
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 200.0,
        )
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 900.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 800.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseActionState.AllIn(actionId = 0L, playerId = PlayerId("0"), betSize = 1000.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 0.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 800.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_SB_Check_and_BB_Bet() {
        // prepare
        every { prefRepository.myPlayerId } returns flowOf("1")
        val pendingBetPerPlayer = emptyMap<PlayerId, Double>()
        val players = listOf(
            PlayerState(id = PlayerId("0"), name = "0", stack = 800.0, isLeaved = false),
            PlayerState(id = PlayerId("1"), name = "1", stack = 800.0, isLeaved = false),
            PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseActionState.Bet(actionId = 0L, playerId = PlayerId("1"), betSize = 200.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                PlayerState(id = PlayerId("0"), name = "0", stack = 800.0, isLeaved = false),
                PlayerState(id = PlayerId("1"), name = "1", stack = 600.0, isLeaved = false),
                PlayerState(id = PlayerId("2"), name = "2", stack = 800.0, isLeaved = false),
            )
        )
    }
}