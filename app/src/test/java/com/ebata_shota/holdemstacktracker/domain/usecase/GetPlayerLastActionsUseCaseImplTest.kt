package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.AllIn
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Blind
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Call
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.Fold
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPlayerLastActionsUseCaseImplTest {
    private lateinit var useCase: GetPlayerLastActionsUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetPlayerLastActionsUseCaseImpl(
            getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
        )
    }

    private fun executeAndAssert(
        phaseList: List<Phase>,
        expected: Map<PlayerId, BetPhaseAction?>
    ) {
        runTest(dispatcher) {
            // execute
            val actual = useCase.invoke(
                playerOrder = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                phaseList = phaseList
            )
            // assert
            assertEquals(expected, actual)
        }
    }

    @Test
    fun standby() {
        // prepare
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to null,
            PlayerId("1") to null,
            PlayerId("2") to null
        )
        executeAndAssert(phaseList, expected)
    }

    @Test
    fun preFlop_allCall() {
        // prepare
        val phaseLists = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf<BetPhaseAction>(
                    Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
                    Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
                    Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
                    Call(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 200),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to Call(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 200),
            PlayerId("1") to Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            PlayerId("2") to Call(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 200),
        )
        executeAndAssert(phaseLists, expected)
    }

    @Test
    fun preFlop_allFold() {
        // prepare
        val phaseLists = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
                    Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
                    Fold(actionId = ActionId(""), playerId = PlayerId("2")),
                    Fold(actionId = ActionId(""), playerId = PlayerId("0")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to Fold(actionId = ActionId(""), playerId = PlayerId("0")),
            PlayerId("1") to Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
            PlayerId("2") to Fold(actionId = ActionId(""), playerId = PlayerId("2")),
        )
        executeAndAssert(phaseLists, expected)
    }

    @Test
    fun preFlop_2AllIn_1Fold() {
        // prepare
        val phaseLists = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(
                phaseId = PhaseId(""),
                actionStateList = listOf(
                    Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100),
                    Blind(actionId = ActionId(""), playerId = PlayerId("1"), betSize = 200),
                    AllIn(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 1000),
                    AllIn(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 1500),
                    Fold(actionId = ActionId(""), playerId = PlayerId("1")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to AllIn(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 1500),
            PlayerId("1") to Fold(actionId = ActionId(""), playerId = PlayerId("1")),
            PlayerId("2") to AllIn(actionId = ActionId(""), playerId = PlayerId("2"), betSize = 1000),
        )
        executeAndAssert(phaseLists, expected)
    }
}