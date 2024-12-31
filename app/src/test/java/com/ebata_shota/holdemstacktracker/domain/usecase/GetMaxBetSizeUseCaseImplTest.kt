package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMaxBetSizeUseCaseImplTest {

    private lateinit var useCase: GetMaxBetSizeUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetMaxBetSizeUseCaseImpl(
            dispatcher = dispatcher
        )
    }

    @Test
    fun getMaxBetSize_empty() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = emptyList()
            )
            val expected = 0
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_all_check() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = listOf(
                    BetPhaseAction.Check(
                        actionId = ActionId(""),
                        playerId = PlayerId("")
                    ),
                    BetPhaseAction.Check(
                        actionId = ActionId(""),
                        playerId = PlayerId("")
                    )
                )
            )
            val expected = 0
            assertEquals(expected, actual)
        }
    }

    private fun createFullActions() = listOf(
        BetPhaseAction.Blind(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 100
        ),
        BetPhaseAction.Blind(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 200
        ),
        BetPhaseAction.Call(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 200
        ),
        BetPhaseAction.Raise(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 400
        ),
        BetPhaseAction.Fold(
            actionId = ActionId(""),
            playerId = PlayerId("")
        ),
        BetPhaseAction.FoldSkip(
            actionId = ActionId(""),
            playerId = PlayerId("")
        ),
        BetPhaseAction.AllIn(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 1000
        ),
        BetPhaseAction.AllIn(
            actionId = ActionId(""),
            playerId = PlayerId(""),
            betSize = 900
        )
    )

    @Test
    fun getMaxBetSize_to_SB() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(1)
            )
            val expected = 100
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_BB() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(2)
            )
            val expected = 200
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_Call() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(3)
            )
            val expected = 200
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_Raise() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(4)
            )
            val expected = 400
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_Fold() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(5)
            )
            val expected = 400
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_Skip() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(6)
            )
            val expected = 400
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_AllIn() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(7)
            )
            val expected = 1000
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getMaxBetSize_to_AllIn_min() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                actionStateList = createFullActions().take(8)
            )
            val expected = 1000
            assertEquals(expected, actual)
        }
    }
}