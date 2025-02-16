package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextGamePlayerListUseCaseImplTest {
    private lateinit var useCase: GetNextGamePlayerStateListUseCaseImpl

    private val firebaseAuthRepository: FirebaseAuthRepository = mockk()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetNextGamePlayerStateListUseCaseImpl(
            firebaseAuthRepository = firebaseAuthRepository,
            dispatcher = dispatcher
        )
    }


    @After
    fun reset() {
        clearMocks(firebaseAuthRepository)
    }

    private fun executeAndAssert(
        pendingBetPerPlayer: Map<PlayerId, Int> = emptyMap(),
        players: List<GamePlayer>,
        action: BetPhaseAction,
        expected: List<GamePlayer>,
    ) {
        runTest(dispatcher) {
            // execute
            val actual: List<GamePlayer> = useCase.invoke(
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
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Call() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Raise() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 400)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 600),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Bet() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Bet(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 900),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_AllIn() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 1000)
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 0),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Check() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Fold() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_Skip() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 1000),
            GamePlayer(id = PlayerId("1"), stack = 1000),
            GamePlayer(id = PlayerId("2"), stack = 1000),
        )
        val action = BetPhaseAction.FoldSkip(actionId = ActionId(""), playerId = PlayerId("0"))
        executeAndAssert(
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 1000),
                GamePlayer(id = PlayerId("1"), stack = 1000),
                GamePlayer(id = PlayerId("2"), stack = 1000),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_Call() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 900),
            GamePlayer(id = PlayerId("1"), stack = 800),
            GamePlayer(id = PlayerId("2"), stack = 800),
        )
        val action = BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 200)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 800),
                GamePlayer(id = PlayerId("1"), stack = 800),
                GamePlayer(id = PlayerId("2"), stack = 800),
            )
        )
    }

    @Test
    fun action_BTN_Call_And_SB_AllIn() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("0"))
        val pendingBetPerPlayer = mapOf(
            PlayerId("0") to 100,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 900),
            GamePlayer(id = PlayerId("1"), stack = 800),
            GamePlayer(id = PlayerId("2"), stack = 800),
        )
        val action = BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 1000)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 0),
                GamePlayer(id = PlayerId("1"), stack = 800),
                GamePlayer(id = PlayerId("2"), stack = 800),
            )
        )
    }

    @Test
    fun action_SB_Check_and_BB_Bet() {
        // prepare
        every { firebaseAuthRepository.myPlayerIdFlow } returns flowOf(PlayerId("1"))
        val pendingBetPerPlayer = emptyMap<PlayerId, Int>()
        val players = listOf(
            GamePlayer(id = PlayerId("0"), stack = 800),
            GamePlayer(id = PlayerId("1"), stack = 800),
            GamePlayer(id = PlayerId("2"), stack = 800),
        )
        val action = BetPhaseAction.Bet(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200)
        executeAndAssert(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = players,
            action = action,
            expected = listOf(
                GamePlayer(id = PlayerId("0"), stack = 800),
                GamePlayer(id = PlayerId("1"), stack = 600),
                GamePlayer(id = PlayerId("2"), stack = 800),
            )
        )
    }
}