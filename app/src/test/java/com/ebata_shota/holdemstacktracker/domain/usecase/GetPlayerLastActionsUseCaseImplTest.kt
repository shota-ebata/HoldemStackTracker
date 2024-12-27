package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction.*
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
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
        useCase = GetPlayerLastActionsUseCaseImpl()
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
            Phase.Standby,
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
            Phase.Standby,
            Phase.PreFlop(
                actionStateList = listOf<BetPhaseAction>(
                    Blind(playerId = PlayerId("0"), betSize = 100.0),
                    Blind(playerId = PlayerId("1"), betSize = 200.0),
                    Call(playerId = PlayerId("2"), betSize = 200.0),
                    Call(playerId = PlayerId("0"), betSize = 200.0),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to Call(playerId = PlayerId("0"), betSize = 200.0),
            PlayerId("1") to Blind(playerId = PlayerId("1"), betSize = 200.0),
            PlayerId("2") to Call(playerId = PlayerId("2"), betSize = 200.0),
        )
        executeAndAssert(phaseLists, expected)
    }

    @Test
    fun preFlop_allFold() {
        // prepare
        val phaseLists = listOf(
            Phase.Standby,
            Phase.PreFlop(
                actionStateList = listOf(
                    Blind(playerId = PlayerId("0"), betSize = 100.0),
                    Blind(playerId = PlayerId("1"), betSize = 200.0),
                    Fold(playerId = PlayerId("2")),
                    Fold(playerId = PlayerId("0")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to Fold(playerId = PlayerId("0")),
            PlayerId("1") to Blind(playerId = PlayerId("1"), betSize = 200.0),
            PlayerId("2") to Fold(playerId = PlayerId("2")),
        )
        executeAndAssert(phaseLists, expected)
    }

    @Test
    fun preFlop_2AllIn_1Fold() {
        // prepare
        val phaseLists = listOf(
            Phase.Standby,
            Phase.PreFlop(
                actionStateList = listOf(
                    Blind(playerId = PlayerId("0"), betSize = 100.0),
                    Blind(playerId = PlayerId("1"), betSize = 200.0),
                    AllIn(playerId = PlayerId("2"), betSize = 1000.0),
                    AllIn(playerId = PlayerId("0"), betSize = 1500.0),
                    Fold(playerId = PlayerId("1")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseAction?>(
            PlayerId("0") to AllIn(playerId = PlayerId("0"), betSize = 1500.0),
            PlayerId("1") to Fold(playerId = PlayerId("1")),
            PlayerId("2") to AllIn(playerId = PlayerId("2"), betSize = 1000.0),
        )
        executeAndAssert(phaseLists, expected)
    }
}