package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyTableState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IsActionRequiredUseCaseImplTest {

    private lateinit var usecase: IsActionRequiredUseCaseImpl

    @Before
    fun setup() {
        usecase = IsActionRequiredUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl(),
            getMaxBetSizeUseCase = GetMaxBetSizeUseCaseImpl()
        )
    }

    @Test
    fun call_getLatestBetPhaseUseCase() {
        // prepare
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCase = mockk()
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        usecase = IsActionRequiredUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseUseCase,
            getMaxBetSizeUseCase = getMaxBetSizeUseCase
        )
        val playerOrder = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2")
        )
        val latestTableState = createDummyTableState(
            playerOrder = playerOrder
        )
        val lastActionList = listOf<BetPhaseActionState>()
        every { getLatestBetPhaseUseCase.invoke(latestTableState) } returns PhaseState.PreFlop(
            phaseId = 0L,
            actionStateList = lastActionList
        )
        every { getMaxBetSizeUseCase.invoke(any()) } returns 0.0f

        // execute
        usecase.invoke(latestTableState)

        // assert
        verify(exactly = 1) {
            getLatestBetPhaseUseCase.invoke(latestTableState)
        }
    }

    @Test
    fun call_getMaxBetSizeUseCase() {
        // prepare
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCase = mockk()
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        usecase = IsActionRequiredUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseUseCase,
            getMaxBetSizeUseCase = getMaxBetSizeUseCase
        )
        val playerOrder = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2")
        )
        val latestTableState = createDummyTableState(
            playerOrder = playerOrder
        )
        val actionStateList = listOf<BetPhaseActionState>()
        every { getLatestBetPhaseUseCase.invoke(latestTableState) } returns PhaseState.PreFlop(
            phaseId = 0L,
            actionStateList = actionStateList
        )
        every { getMaxBetSizeUseCase.invoke(any()) } returns 0.0f

        // execute
        usecase.invoke(latestTableState)

        // assert
        verify(exactly = 1) {
            getMaxBetSizeUseCase.invoke(actionStateList.takeLast(playerOrder.size))
        }
    }

    private fun executeAndAssert(
        actionStateList: List<BetPhaseActionState>,
        playerOrder: List<PlayerId> = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2")
        ),
        expected: Boolean
    ) {
        val latestTableState = createDummyTableState(
            playerOrder = playerOrder,
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = actionStateList)
            )
        )

        // execute
        val actual: Boolean = usecase.invoke(latestTableState)

        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun preFlop_empty_action() {
        // prepare
        val actionStateList = emptyList<BetPhaseActionState>()
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_SB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
            BetPhaseActionState.Blind(actionId = 3L, playerId = PlayerId("0"), betSize = 200.0f),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
            BetPhaseActionState.Call(actionId = 0L, playerId = PlayerId("0"), betSize = 200.0f),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
            BetPhaseActionState.Fold(actionId = 0L, playerId = PlayerId("0")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Raise() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Raise(actionId = 2L, playerId = PlayerId("2"), betSize = 400.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Raise(actionId = 2L, playerId = PlayerId("2"), betSize = 400.0f),
            BetPhaseActionState.Call(actionId = 3L, playerId = PlayerId("0"), betSize = 400.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call_And_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.Raise(actionId = 2L, playerId = PlayerId("2"), betSize = 400.0f),
            BetPhaseActionState.Call(actionId = 3L, playerId = PlayerId("0"), betSize = 400.0f),
            BetPhaseActionState.Call(actionId = 4L, playerId = PlayerId("1"), betSize = 400.0f),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.AllIn(actionId = 2L, playerId = PlayerId("2"), betSize = 1000.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.AllIn(actionId = 2L, playerId = PlayerId("2"), betSize = 1000.0f),
            BetPhaseActionState.AllIn(actionId = 3L, playerId = PlayerId("0"), betSize = 1500.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn_And_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
            BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0f),
            BetPhaseActionState.AllIn(actionId = 2L, playerId = PlayerId("2"), betSize = 1000.0f),
            BetPhaseActionState.AllIn(actionId = 3L, playerId = PlayerId("0"), betSize = 1500.0f),
            BetPhaseActionState.Fold(actionId = 4L, playerId = PlayerId("1")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Check(actionId = 1L, playerId = PlayerId("1")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Check(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Check(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Check(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Fold_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Fold(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Check(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Skip() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Check(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Skip(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Skip_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Skip(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Check(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Bet() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(actionId = 0L, playerId = PlayerId("0")),
            BetPhaseActionState.Check(actionId = 1L, playerId = PlayerId("1")),
            BetPhaseActionState.Bet(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0f),
        )
        executeAndAssert(actionStateList, expected = true)
    }
}