package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGameState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
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
            getMaxBetSize = GetMaxBetSizeUseCaseImpl()
        )
    }

    @Test
    fun call_getMaxBetSizeUseCase() {
        // prepare
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCase = mockk()
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        usecase = IsActionRequiredUseCaseImpl(
            getMaxBetSize = getMaxBetSizeUseCase
        )
        val playerOrder = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2")
        )
        val latestGameState = createDummyGameState()
        val actionStateList = listOf<BetPhaseActionState>()
        every { getLatestBetPhaseUseCase.invoke(latestGameState) } returns PhaseState.PreFlop(
            actionStateList = actionStateList
        )
        every { getMaxBetSizeUseCase.invoke(any()) } returns 0.0

        // execute
        usecase.invoke(
            playerOrder = playerOrder,
            actionStateList = actionStateList
        )

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
        // execute
        val actual: Boolean = usecase.invoke(
            playerOrder = playerOrder,
            actionStateList = actionStateList
        )

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
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Call(playerId = PlayerId("2"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Call(playerId = PlayerId("2"), betSize = 200.0),
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Fold(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Fold(playerId = PlayerId("2")),
            BetPhaseActionState.Call(playerId = PlayerId("0"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Fold(playerId = PlayerId("2")),
            BetPhaseActionState.Fold(playerId = PlayerId("0")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Raise() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Raise(playerId = PlayerId("2"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Raise(playerId = PlayerId("2"), betSize = 400.0),
            BetPhaseActionState.Call(playerId = PlayerId("0"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call_And_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.Raise(playerId = PlayerId("2"), betSize = 400.0),
            BetPhaseActionState.Call(playerId = PlayerId("0"), betSize = 400.0),
            BetPhaseActionState.Call(playerId = PlayerId("1"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.AllIn(playerId = PlayerId("2"), betSize = 1000.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.AllIn(playerId = PlayerId("2"), betSize = 1000.0),
            BetPhaseActionState.AllIn(playerId = PlayerId("0"), betSize = 1500.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn_And_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
            BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 200.0),
            BetPhaseActionState.AllIn(playerId = PlayerId("2"), betSize = 1000.0),
            BetPhaseActionState.AllIn(playerId = PlayerId("0"), betSize = 1500.0),
            BetPhaseActionState.Fold(playerId = PlayerId("1")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Check(playerId = PlayerId("1")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Check(playerId = PlayerId("1")),
            BetPhaseActionState.Check(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Check(playerId = PlayerId("1")),
            BetPhaseActionState.Fold(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Fold_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Fold(playerId = PlayerId("1")),
            BetPhaseActionState.Check(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Skip() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Check(playerId = PlayerId("1")),
            BetPhaseActionState.FoldSkip(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Skip_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.FoldSkip(playerId = PlayerId("1")),
            BetPhaseActionState.Check(playerId = PlayerId("2")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Bet() {
        // prepare
        val actionStateList = listOf(
            BetPhaseActionState.Check(playerId = PlayerId("0")),
            BetPhaseActionState.Check(playerId = PlayerId("1")),
            BetPhaseActionState.Bet(playerId = PlayerId("2"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }
}