package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextGamePlayerListUseCaseImplTest {
    private lateinit var usecase: GetNextGamePlayerStateListUseCaseImpl

    private val firebaseAuthRepository: FirebaseAuthRepository = mockk()

    @Before
    fun setup() {
        usecase = GetNextGamePlayerStateListUseCaseImpl(
            firebaseAuthRepository = firebaseAuthRepository
        )
    }


    @After
    fun reset() {
        clearMocks(firebaseAuthRepository)
    }

    private fun executeAndAssert(
        pendingBetPerPlayer: Map<PlayerId, Double> = emptyMap(),
        players: List<GamePlayer>,
        action: BetPhaseAction,
        expected: List<GamePlayer>,
    ) {
        runTest {
            // execute
            val actual: List<GamePlayer> = usecase.invoke(
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
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Call() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Call(playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Raise() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Raise(playerId = PlayerId("0"), betSize = 400.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 600.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Bet() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Bet(playerId = PlayerId("0"), betSize = 100.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_AllIn() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.AllIn(playerId = PlayerId("0"), betSize = 1000.0)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 0.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Check() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Check(playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Fold() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.Fold(playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_Skip() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
        )
        val action = BetPhaseAction.FoldSkip(playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 1000.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 1000.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_Call() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 200.0,
        )
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 900.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 800.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseAction.Call(playerId = PlayerId("0"), betSize = 200.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 800.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 800.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_AllIn() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 200.0,
        )
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 900.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 800.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseAction.AllIn(playerId = PlayerId("0"), betSize = 1000.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 0.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 800.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
            )
        )
    }

    @Test
    fun action_SB_Check_and_BB_Bet() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("1"))
        val pendingBetPerPlayer = emptyMap<PlayerId, Double>()
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 800.0, isLeaved = false),
            GamePlayer(id = PlayerId("1"), stack = 800.0, isLeaved = false),
            GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
        )
        val action = BetPhaseAction.Bet(playerId = PlayerId("1"), betSize = 200.0)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 800.0, isLeaved = false),
                GamePlayer(id = PlayerId("1"), stack = 600.0, isLeaved = false),
                GamePlayer(id = PlayerId("2"), stack = 800.0, isLeaved = false),
            )
        )
    }
}