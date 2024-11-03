package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPendingBetPerPlayerUseCaseImplTest {
    private lateinit var usecase: GetPendingBetPerPlayerUseCaseImpl

    @Before
    fun setup() {
        usecase = GetPendingBetPerPlayerUseCaseImpl(
            getMaxBetSizeUseCase = GetMaxBetSizeUseCaseImpl()
        )
    }

    @Test
    fun getMaxBetSize_call_getMaxBetSizeUseCase() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
        )
        val playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        every { getMaxBetSizeUseCase.invoke(any()) } returns 200.0f
        usecase = GetPendingBetPerPlayerUseCaseImpl(
            getMaxBetSizeUseCase = getMaxBetSizeUseCase
        )

        // execute
        usecase.invoke(
            playerOrder = playerOrder,
            actionStateList = actionStateList
        )

        // assert
        playerOrder.forEach { playerId ->
            val playerActionList = actionStateList.filter { it.playerId == playerId }
            verify(exactly = 1) { getMaxBetSizeUseCase.invoke(playerActionList) }
        }
    }

    private fun executeAndAssert(
        playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        ),
        actionStateList: List<BetPhaseActionState>,
        expected: Map<PlayerId, Float>
    ) {
        val actual: Map<PlayerId, Float> = usecase.invoke(
            playerOrder = playerOrder,
            actionStateList = actionStateList
        )
        assertEquals(expected, actual)
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100.0f,
                PlayerId("1") to 200.0f,
                PlayerId("2") to 200.0f,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 100.0f,
                PlayerId("1") to 200.0f,
            ),
        )
    }

    @Test
    fun preFlop_BB_BB_And_BTN_Call_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 200.0f),
        )
        executeAndAssert(
            actionStateList = actionStateList,
            expected = mapOf(
                PlayerId("0") to 200.0f,
                PlayerId("1") to 200.0f,
                PlayerId("2") to 200.0f,
            ),
        )
    }
}