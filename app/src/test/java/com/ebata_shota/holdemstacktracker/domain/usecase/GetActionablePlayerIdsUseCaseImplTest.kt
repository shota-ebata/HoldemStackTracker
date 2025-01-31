package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetActionablePlayerIdsUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetActionablePlayerIdsUseCaseImplTest {

    private lateinit var useCase: GetActionablePlayerIdsUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetActionablePlayerIdsUseCaseImpl(
            getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
                getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
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
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2")
        ),
        expected: List<PlayerId>,
    ) {
        // execute
        runTest(dispatcher) {
            val actual: List<PlayerId> = useCase.invoke(
                playerOrder = playerOrder,
                phaseList = phaseList,
            )

            // assert
            assertEquals(expected, actual)
        }
    }

    @Test
    fun flop_SB_allin_skip_BB_check_BTN_fold() {
        executeAndAssert(
            phaseList = listOf(
                Phase.Standby(PhaseId("")),
                Phase.PreFlop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("1"), betSize = 1),
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("2"), betSize = 2),
                        BetPhaseAction.Bet(ActionId(""), playerId = PlayerId("0"), betSize = 4),
                        BetPhaseAction.AllIn(ActionId(""), playerId = PlayerId("1"), betSize = 100),
                        BetPhaseAction.Call(ActionId(""), playerId = PlayerId("2"), betSize = 100),
                        BetPhaseAction.Call(ActionId(""), playerId = PlayerId("0"), betSize = 100),
                    )
                ),
                Phase.Flop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.AllInSkip(ActionId(""), playerId = PlayerId("1")),
                        BetPhaseAction.Check(ActionId(""), playerId = PlayerId("2")),
                        BetPhaseAction.Fold(ActionId(""), playerId = PlayerId("0")),
                    )
                )
            ),
            expected = listOf(PlayerId("2"),),
        )
    }

    @Test
    fun flop_SB_allin_skip_BB_fold() {
        executeAndAssert(
            phaseList = listOf(
                Phase.Standby(PhaseId("")),
                Phase.PreFlop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("1"), betSize = 1),
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("2"), betSize = 2),
                        BetPhaseAction.Bet(ActionId(""), playerId = PlayerId("0"), betSize = 4),
                        BetPhaseAction.AllIn(ActionId(""), playerId = PlayerId("1"), betSize = 100),
                        BetPhaseAction.Call(ActionId(""), playerId = PlayerId("2"), betSize = 100),
                        BetPhaseAction.Call(ActionId(""), playerId = PlayerId("0"), betSize = 100),
                    )
                ),
                Phase.Flop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.AllInSkip(ActionId(""), playerId = PlayerId("1")),
                        BetPhaseAction.Fold(ActionId(""), playerId = PlayerId("2")),
                    )
                )
            ),
            expected = listOf(PlayerId("0"),),
        )
    }

    @Test
    fun preFlop_BTN_fold() {
        executeAndAssert(
            phaseList = listOf(
                Phase.Standby(PhaseId("")),
                Phase.PreFlop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("1"), betSize = 1),
                        BetPhaseAction.Blind(ActionId(""), playerId = PlayerId("0"), betSize = 2),
                        BetPhaseAction.Fold(ActionId(""), playerId = PlayerId("1")),
                    )
                ),
            ),
            playerOrder = listOf(
                PlayerId("0"),
                PlayerId("1"),
            ),
            expected = listOf(PlayerId("0"),),
        )
    }
}