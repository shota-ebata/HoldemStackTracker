package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMinRaiseSizeUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMinRaiseSizeUseCaseImplTest {

    private lateinit var useCase: GetMinRaiseSizeUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetMinRaiseSizeUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher,
        )
    }

    @Test
    fun test_preFlop_open_min() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(
                    actionStateList = listOf(
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId1"),
                            betSize = 1.0
                        ),
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId2"),
                            betSize = 2.0
                        )
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 4.0
            assertEquals(expected, actual, 0.0)
        }
    }

    @Test
    fun test_preFlop_reRaise_min() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(
                    actionStateList = listOf(
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId1"),
                            betSize = 1.0
                        ),
                        BetPhaseAction.Bet(
                            playerId = PlayerId("PlayerId2"),
                            betSize = 2.0
                        ),
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId3"),
                            betSize = 5.0
                        )
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 8.0
            assertEquals(expected, actual, 0.0)
        }
    }

    @Test
    fun test_preFlop_reRaise_min_after_small_allin() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(
                    actionStateList = listOf(
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId1"),
                            betSize = 1.0
                        ),
                        BetPhaseAction.Blind(
                            playerId = PlayerId("PlayerId2"),
                            betSize = 2.0
                        ),
                        BetPhaseAction.Bet(
                            playerId = PlayerId("PlayerId3"),
                            betSize = 5.0
                        ),
                        BetPhaseAction.AllIn(
                            playerId = PlayerId("PlayerId4"),
                            betSize = 3.0
                        )
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 8.0
            assertEquals(expected, actual, 0.0)
        }
    }

    @Test
    fun test_flop() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.Flop(
                    actionStateList = listOf()
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 2.0
            assertEquals(expected, actual, 0.0)
        }
    }

    @Test
    fun test_flop_open() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.Flop(
                    actionStateList = listOf(
                        BetPhaseAction.Bet(
                            playerId = PlayerId("PlayerId1"),
                            betSize = 2.0
                        ),
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 4.0
            assertEquals(expected, actual, 0.0)
        }
    }

    @Test
    fun test_flop_open5() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.Flop(
                    actionStateList = listOf(
                        BetPhaseAction.Bet(
                            playerId = PlayerId("PlayerId1"),
                            betSize = 5.0
                        ),
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Double = useCase.invoke(
                game = game,
                minBetSize = 2.0
            )
            val expected = 10.0
            assertEquals(expected, actual, 0.0)
        }
    }
}