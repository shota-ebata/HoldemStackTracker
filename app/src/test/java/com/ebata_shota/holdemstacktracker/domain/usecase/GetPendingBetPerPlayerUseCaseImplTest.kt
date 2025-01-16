package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
        )
        val playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        coEvery { getMaxBetSizeUseCase.invoke(any()) } returns 200
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
        expected: Map<PlayerId, Int>,
    ) {
        runTest(dispatcher) {
            val actual: Map<PlayerId, Int> = useCase.invoke(
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
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100,
                PlayerId("1") to 200,
                PlayerId("2") to 200,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("2")),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100,
                PlayerId("1") to 200,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Call_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 200),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 200,
                PlayerId("1") to 200,
                PlayerId("2") to 200,
            ),
        )
    }

    @Test
    fun fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
            BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 400),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("1")),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 400),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 400,
                PlayerId("1") to 200,
                PlayerId("2") to 400,
            ),
        )
    }
}