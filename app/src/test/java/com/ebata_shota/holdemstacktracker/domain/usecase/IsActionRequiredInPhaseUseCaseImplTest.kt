package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetActionablePlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNotFoldPlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredInPhaseUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IsActionRequiredInPhaseUseCaseImplTest {

    private lateinit var useCase: IsActionRequiredInPhaseUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // TODO: hilt
        useCase = IsActionRequiredInPhaseUseCaseImpl(
            getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                dispatcher = dispatcher
            ),
            getPendingBetSize = GetPendingBetSizeUseCaseImpl(
                getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                    getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                        dispatcher = dispatcher
                    ),
                    dispatcher = dispatcher
                ),
                dispatcher = dispatcher
            ),
            getNotFoldPlayerIds = GetNotFoldPlayerIdsUseCaseImpl(
                getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
                    getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                        dispatcher = dispatcher
                    ),
                    dispatcher = dispatcher,
                ),
                dispatcher = dispatcher,
            ),
            getActionablePlayerIds = GetActionablePlayerIdsUseCaseImpl(
                getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
                    getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                        dispatcher = dispatcher
                    ),
                    dispatcher = dispatcher,
                ),
                dispatcher = dispatcher,
            ),
            dispatcher = dispatcher,
        )
    }

    private fun executeAndAssert(
        phaseList: List<Phase>,
        playerOrder: List<PlayerId> = listOf(
            PlayerId("SB"),
            PlayerId("BB"),
            PlayerId("BTN")
        ),
        expected: Boolean,
    ) {
        // execute
        runTest(dispatcher) {
            val actual: Boolean = useCase.invoke(
                playerOrder = playerOrder,
                phaseList = phaseList
            )

            // assert
            assertEquals(expected, actual)
        }
    }

    @Test
    fun preFlop_empty_action() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = emptyList()
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_SB_Blind() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                )
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BB_Blind() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                )
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                )
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 200
                    ),
                )
            )
        )
        executeAndAssert(phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Call_And_SB_Call_BB_CHECK() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
                )
            )
        )
        executeAndAssert(phaseList, expected = false)
    }

    @Test
    fun preFlop_BTN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BTN")),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 200),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Fold_And_SB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BTN")),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("SB")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun preFlop_BTN_Raise() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 400),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 400),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 400),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_Raise_And_SB_Call_And_BB_Call() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 400),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 400),
            BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 400),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun preFlop_BTN_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 1000),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 1000),
            BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 1500),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun preFlop_BTN_AllIn_And_SB_AllIn_And_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
            BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 1000),
            BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 1500),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BB")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun preFlop_2_BB_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), 2),
            BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BTN"), 1),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BB")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(
            phaseList = phaseList,
            playerOrder = listOf(
                PlayerId("BB"),
                PlayerId("BTN")
            ),
            expected = false
        )
    }

    @Test
    fun flop_SB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 100),
                    BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 200),
                    BetPhaseAction.Raise(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 400),
                    BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("SB"), betSize = 400),
                    BetPhaseAction.Call(actionId = ActionId(""), playerId = PlayerId("BB"), betSize = 400),
                )
            ),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Fold() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Fold_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Skip() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.FoldSkip(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Skip_BIN_Check() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.FoldSkip(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    @Test
    fun flop_SB_Check_BB_Check_BIN_Bet() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Bet(actionId = ActionId(""), playerId = PlayerId("BTN"), betSize = 200),
        )
        val phaseList = listOf(
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }

    @Test
    fun flop_() {
        // prepare
        val actionStateList = listOf(
            BetPhaseAction.AllInSkip(actionId = ActionId(""), playerId = PlayerId("SB")),
            BetPhaseAction.Check(actionId = ActionId(""), playerId = PlayerId("BB")),
            BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BTN")),
        )
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                    BetPhaseAction.AllIn(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 10000
                    ),
                )
            ),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = actionStateList
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    /**
     * PreFlop
     *  SB:  Blind(100)
     *  BB:  Blind(200)
     *  BTN: Call
     *  SB:  AllIn(10000)
     *  BB:  Call
     *  BTN  Call
     * Flop
     *  BB: Fold
     *
     * この時
     * isActionRequiredInPhase = true
     */
    @Test
    fun flop_2() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                    BetPhaseAction.AllIn(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 10000
                    ),
                )
            ),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.AllInSkip(actionId = ActionId(""), playerId = PlayerId("SB")),
                    BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BB")),
                )
            )
        )
        executeAndAssert(phaseList = phaseList, expected = false)
    }

    /**
     * PreFlop
     *  SB:  Blind(100)
     *  BB:  Blind(200)
     *  BTN: Call
     *  SB:  AllIn(10000)
     *  BB:  Call
     *  BTN  Call
     * Flop
     *  BB: AllIn(10000)
     *
     * この時
     * isActionRequiredInPhase = true
     */
    @Test
    fun flop_allin_2() {
        // prepare
        val phaseList = listOf(
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 100
                    ),
                    BetPhaseAction.Blind(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 200
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 200
                    ),
                    BetPhaseAction.AllIn(
                        actionId = ActionId(""),
                        playerId = PlayerId("SB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BB"),
                        betSize = 10000
                    ),
                    BetPhaseAction.Call(
                        actionId = ActionId(""),
                        playerId = PlayerId("BTN"),
                        betSize = 10000
                    ),
                )
            ),
            Phase.Flop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    BetPhaseAction.AllInSkip(actionId = ActionId(""), playerId = PlayerId("SB")),
                    BetPhaseAction.AllIn(actionId = ActionId(""), playerId = PlayerId("BB"), 10000),
                )
            )
        )
        executeAndAssert(phaseList = phaseList, expected = true)
    }
}