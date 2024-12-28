package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetSizeImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IsActionRequiredUseCaseImplTest {

    private lateinit var useCase: IsActionRequiredUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // TODO: hilt
        useCase = IsActionRequiredUseCaseImpl(
            getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                dispatcher = dispatcher
            ),
            getPendingBetSize = GetPendingBetSizeImpl(
                getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                    getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                        dispatcher = dispatcher
                    ),
                    dispatcher = dispatcher
                ),
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
        )
    }

    @Test
    fun call_getMaxBetSizeUseCase() {
        // prepare
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCase = mockk()
        val getMaxBetSizeUseCase: GetMaxBetSizeUseCase = mockk()
        // TODO: Hilt
        useCase = IsActionRequiredUseCaseImpl(
            getMaxBetSize = getMaxBetSizeUseCase,
            getPendingBetSize = GetPendingBetSizeImpl(
                getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                    getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                        dispatcher = dispatcher
                    ),
                    dispatcher = dispatcher
                ),
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
        )
        val playerOrder = listOf(
            PlayerId("SB"),
            PlayerId("BB"),
            PlayerId("BTN")
        )
        val latestGame = createDummyGame()
        val actionStateList = listOf<BetPhaseAction>()
        coEvery { getLatestBetPhaseUseCase.invoke(latestGame) } returns Phase.PreFlop(
            actionStateList = actionStateList
        )
        coEvery { getMaxBetSizeUseCase.invoke(any()) } returns 0

        // execute
        runTest(dispatcher) {
            useCase.invoke(
                playerOrder = playerOrder,
                actionStateList = actionStateList
            )
        }

        // assert
        coVerify(exactly = 1) {
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
        runTest(dispatcher) {
            val actual: Boolean = useCase.invoke(
                playerOrder = playerOrder,
                actionStateList = actionStateList
            )

            // assert
            assertEquals(expected, actual)
        }
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
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BB_Blind() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call_BB_CHECK() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Call(playerId = PlayerId("BTN"), betSize = 200),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200),
            BetPhaseAction.Check(playerId = PlayerId("BB")),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 200),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(playerId = PlayerId("BTN")),
            BetPhaseAction.Fold(playerId = PlayerId("SB")),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 400),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call_And_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(playerId = PlayerId("BTN"), betSize = 400),
            BetPhaseAction.Call(playerId = PlayerId("SB"), betSize = 400),
            BetPhaseAction.Call(playerId = PlayerId("BB"), betSize = 400),
        )
        executeAndAssert(actionStateList, expected = false)
    }

    @Test
    fun preFlop_BTN_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000),
            BetPhaseAction.AllIn(playerId = PlayerId("SB"), betSize = 1500),
        )
        executeAndAssert(actionStateList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn_And_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(playerId = PlayerId("BTN"), betSize = 1000),
            BetPhaseAction.AllIn(playerId = PlayerId("SB"), betSize = 1500),
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
            BetPhaseAction.Bet(playerId = PlayerId("BTN"), betSize = 200),
        )
        executeAndAssert(actionStateList, expected = true)
    }
}