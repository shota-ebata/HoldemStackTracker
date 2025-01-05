package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Blind
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Call
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetCurrentPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastPhaseAsBetPhaseUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCurrentPlayerIdUseCaseImplTest {
    private lateinit var useCase: GetCurrentPlayerIdUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetCurrentPlayerIdUseCaseImpl(
            getLastPhaseAsBetPhase = GetLastPhaseAsBetPhaseUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher,
        )
    }

    private fun executeAndAssert(
        actionStateList: List<BetPhaseAction>,
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        expected: PlayerId
    ) {
        val currentBetPhase = Phase.PreFlop(
            phaseId = PhaseId(""),
            actionStateList = actionStateList
        )
        runTest(dispatcher) {
            val actual = useCase.invoke(
                btnPlayerId = btnPlayerId,
                playerOrder = playerOrder,
                currentBetPhase = currentBetPhase
            )
            assertEquals(expected, actual)
        }
    }


    @Test
    fun test_empty() {
        val btnPlayerId = PlayerId("PlayerId2")
        val actionStateList = listOf<BetPhaseAction>()
        val expected = PlayerId("PlayerId0")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1"),
                PlayerId("PlayerId2")
            ),
            expected = expected
        )
    }

    @Test
    fun test_after_sb() {
        val btnPlayerId = PlayerId("PlayerId2")
        val actionStateList = listOf<BetPhaseAction>(
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId0"), betSize = 100),
        )
        val expected = PlayerId("PlayerId1")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1"),
                PlayerId("PlayerId2")
            ),
            expected = expected
        )
    }

    @Test
    fun test_after_bb() {
        val btnPlayerId = PlayerId("PlayerId2")
        val actionStateList = listOf<BetPhaseAction>(
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId0"), betSize = 100),
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId1"), betSize = 200),
        )
        val expected = PlayerId("PlayerId2")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1"),
                PlayerId("PlayerId2")
            ),
            expected = expected
        )
    }

    @Test
    fun test_after_btn() {
        val btnPlayerId = PlayerId("PlayerId2")
        val actionStateList = listOf<BetPhaseAction>(
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId0"), betSize = 100),
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId1"), betSize = 200),
            Call(actionId = ActionId(""), playerId = PlayerId("PlayerId2"), betSize = 200),
        )
        val expected = PlayerId("PlayerId0")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1"),
                PlayerId("PlayerId2")
            ),
            expected = expected
        )
    }

    @Test
    fun test1() {
        val btnPlayerId = PlayerId("PlayerId2")
        val actionStateList = listOf<BetPhaseAction>(
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId0"), betSize = 100),
            Blind(actionId = ActionId(""), playerId = PlayerId("PlayerId1"), betSize = 200),
            Call(actionId = ActionId(""), playerId = PlayerId("PlayerId2"), betSize = 200),
            Call(actionId = ActionId(""), playerId = PlayerId("PlayerId0"), betSize = 200),
        )
        val expected = PlayerId("PlayerId1")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1"),
                PlayerId("PlayerId2")
            ),
            expected = expected
        )
    }

    @Test
    fun test_2players() {
        val btnPlayerId = PlayerId("PlayerId0")
        val actionStateList = listOf<BetPhaseAction>()
        val expected = PlayerId("PlayerId0")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1")
            ),
            expected = expected
        )
    }

    @Test
    fun test_2player_2() {
        val btnPlayerId = PlayerId("PlayerId1")
        val actionStateList = listOf<BetPhaseAction>()
        val expected = PlayerId("PlayerId1")
        executeAndAssert(
            actionStateList = actionStateList,
            btnPlayerId = btnPlayerId,
            playerOrder = listOf(
                PlayerId("PlayerId0"),
                PlayerId("PlayerId1")
            ),
            expected = expected
        )
    }
}