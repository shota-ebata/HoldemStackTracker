package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Blind
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Call
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetCurrentPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCurrentPlayerIdUseCaseImplTest {
    private lateinit var usecase: GetCurrentPlayerIdUseCaseImpl

    @Before
    fun setup() {
        usecase = GetCurrentPlayerIdUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl()
        )
    }

    @Test
    fun getCurrentPlayerId_call_getLatestBetPhase() {
        val getLatestBetPhaseMock = mockk<GetLatestBetPhaseUseCaseImpl>()
        every { getLatestBetPhaseMock.invoke(any()) } returns (Phase.PreFlop(actionStateList = emptyList()))
        usecase = GetCurrentPlayerIdUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseMock
        )
        val game = createDummyGame(
            players = setOf(
                GamePlayer(
                    id = PlayerId("PlayerId0"),
                    stack = 1000.0,
                    isLeaved = false
                ),
                GamePlayer(
                    id = PlayerId("PlayerId1"),
                    stack = 1000.0,
                    isLeaved = false
                ),
                GamePlayer(
                    id = PlayerId("PlayerId2"),
                    stack = 1000.0,
                    isLeaved = false
                )
            )
        )
        val playerOrder = listOf(
            PlayerId("PlayerId0"),
            PlayerId("PlayerId1"),
            PlayerId("PlayerId2")
        )
        runTest {
            usecase.invoke(
                btnPlayerId = PlayerId("PlayerId0"),
                playerOrder = playerOrder,
                game = game
            )
        }
        verify(exactly = 1) { getLatestBetPhaseMock.invoke(game) }
    }

    private fun executeAndAssert(
        actionStateList: List<BetPhaseAction>,
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        expected: PlayerId
    ) {
        val game = createDummyGame(
            players = setOf(
                GamePlayer(
                    id = PlayerId("PlayerId0"),
                    stack = 1000.0,
                    isLeaved = false
                ),
                GamePlayer(
                    id = PlayerId("PlayerId1"),
                    stack = 1000.0,
                    isLeaved = false
                ),
                GamePlayer(
                    id = PlayerId("PlayerId2"),
                    stack = 1000.0,
                    isLeaved = false
                )
            ),
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(
                    actionStateList = actionStateList
                )
            )
        )
        runTest {
            val actual = usecase.invoke(
                btnPlayerId = btnPlayerId,
                playerOrder = playerOrder,
                game = game
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
            Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
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
            Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
            Blind(playerId = PlayerId("PlayerId1"), betSize = 200.0),
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
            Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
            Blind(playerId = PlayerId("PlayerId1"), betSize = 200.0),
            Call(playerId = PlayerId("PlayerId2"), betSize = 200.0),
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
            Blind(playerId = PlayerId("PlayerId0"), betSize = 100.0),
            Blind(playerId = PlayerId("PlayerId1"), betSize = 200.0),
            Call(playerId = PlayerId("PlayerId2"), betSize = 200.0),
            Call(playerId = PlayerId("PlayerId0"), betSize = 200.0),
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