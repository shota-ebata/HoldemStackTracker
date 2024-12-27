package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPendingBetPerPlayerUseCaseImplTest {
    private lateinit var useCase: GetPendingBetPerPlayerUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetPendingBetPerPlayerUseCaseImpl(
            getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
        )
    }

    @Test
    fun getMaxBetSize_call_getMaxBetSizeUseCase() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("2"), betSize = 200.0),
        )
        val playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        coEvery { getMaxBetSizeUseCase.invoke(any()) } returns 200.0
        useCase = GetPendingBetPerPlayerUseCaseImpl(
            getMaxBetSize = getMaxBetSizeUseCase,
            dispatcher = dispatcher
        )

        // execute
        runTest(dispatcher) {
            useCase.invoke(
                playerOrder = playerOrder,
                actionStateList = actionStateList
            )
        }

        // assert
        playerOrder.forEach { playerId ->
            val playerActionList = actionStateList.filter { it.playerId == playerId }
            coVerify(exactly = 1) { getMaxBetSizeUseCase.invoke(playerActionList) }
        }
    }

    private fun executeAndAssert(
        playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        ),
        actionStateList: List<BetPhaseAction>,
        expected: Map<PlayerId, Double>
    ) {
        runTest(dispatcher) {
            val actual: Map<PlayerId, Double> = useCase.invoke(
                playerOrder = playerOrder,
                actionStateList = actionStateList
            )
            assertEquals(expected, actual)
        }
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("2"), betSize = 200.0),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100.0,
                PlayerId("1") to 200.0,
                PlayerId("2") to 200.0,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseAction.Fold(playerId = PlayerId("2")),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100.0,
                PlayerId("1") to 200.0,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Call_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("2"), betSize = 200.0),
            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 200.0),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 200.0,
                PlayerId("1") to 200.0,
                PlayerId("2") to 200.0,
            ),
        )
    }
}