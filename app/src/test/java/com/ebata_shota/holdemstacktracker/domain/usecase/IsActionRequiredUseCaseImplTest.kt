package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
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
            getMaxBetSize = GetMaxBetSizeUseCaseImpl(),
            getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                getMaxBetSize = GetMaxBetSizeUseCaseImpl()
            )
        )
    }

    @Test
    fun call_getMaxBetSizeUseCase() {
        // prepare
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCase = mockk()
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        usecase = IsActionRequiredUseCaseImpl(
            getMaxBetSize = getMaxBetSizeUseCase,
            getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                getMaxBetSize = GetMaxBetSizeUseCaseImpl()
            )
        )
        val playerOrder = listOf(
            PlayerId("SB"),
            PlayerId("BB"),
            PlayerId("BTN")
        )
        val latestGame = createDummyGame()
        val actionStateList = listOf<BetPhaseAction>()
        every { getLatestBetPhaseUseCase.invoke(latestGame) } returns Phase.PreFlop(
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
        actionStateList: List<BetPhaseAction>,
        playerOrder: List<PlayerId> = listOf(
            PlayerId("SB"),
            PlayerId("BB"),
            PlayerId("BTN")
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
        val actionStateList = emptyList<BetPhaseAction>()
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_SB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call_BB_CHECK() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200.0),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200.0),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
            BetPhaseAction.Fold(playerId = PlayerId("SB")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400.0),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call_And_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400.0),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 400.0),
            BetPhaseAction.Call(playerId = PlayerId("BB"), betSize = 400.0),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000.0),
            BetPhaseAction.AllIn(playerId = PlayerId("SB"), betSize = 1500.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn_And_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100.0),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200.0),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000.0),
            BetPhaseAction.AllIn(playerId = PlayerId("SB"), betSize = 1500.0),
            BetPhaseAction.Fold(playerId = PlayerId("BB")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
            BetPhaseAction.Check(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Fold_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Fold(playerId = PlayerId("BB")),
            BetPhaseAction.Check(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Skip() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
            BetPhaseAction.FoldSkip(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Skip_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.FoldSkip(playerId = PlayerId("BB")),
            BetPhaseAction.Check(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Bet() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(playerId = PlayerId("SB")),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
            BetPhaseAction.Bet(playerId = PlayerId("BTN"), betSize = 200.0),
        )
        executeAndAssert(actionStateList, expected = true)
    }
}