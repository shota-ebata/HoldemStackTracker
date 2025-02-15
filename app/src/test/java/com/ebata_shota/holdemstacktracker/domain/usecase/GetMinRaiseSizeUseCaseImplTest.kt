package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastPhaseAsBetPhaseUseCaseImpl
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
            getLastPhaseAsBetPhase = GetLastPhaseAsBetPhaseUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher,
        )
    }

    @Test
    fun test_preFlop_open_min() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId1"),
                        betSize = 1
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId2"),
                        betSize = 2
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 4
            assertEquals(expected, actual)
        }
    }

    @Test
    fun test_preFlop_reRaise_min() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId1"),
                        betSize = 1
                    ),
                    BetPhaseAction.Bet(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId2"),
                        betSize = 2
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId3"),
                        betSize = 5
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 8
            assertEquals(expected, actual)
        }
    }

    @Test
    fun test_preFlop_reRaise_min_after_small_allin() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId1"),
                        betSize = 1
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId2"),
                        betSize = 2
                    ),
                    BetPhaseAction.Bet(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId3"),
                        betSize = 5
                    ),
                    BetPhaseAction.AllIn(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId4"),
                        betSize = 3
                    )
                )
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 8
            assertEquals(expected, actual)
        }
    }

    @Test
    fun test_flop() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = listOf()
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 2
            assertEquals(expected, actual)
        }
    }

    @Test
    fun test_flop_open() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId(""),),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Bet(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId1"),
                        betSize = 2
                    ),
                )
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 4
            assertEquals(expected, actual)
        }
    }

    @Test
    fun test_flop_open5() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Bet(
                        actionId = ActionId(""),
                        playerId = PlayerId("PlayerId1"),
                        betSize = 5
                    ),
                )
            )
        )
        runTest(dispatcher) {
            val actual: Int = useCase.invoke(
                phaseList = phaseList,
                minBetSize = 2
            )
            val expected = 10
            assertEquals(expected, actual)
        }
    }
}