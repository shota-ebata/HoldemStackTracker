package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState.*
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPlayerLastActionsUseCaseImplTest {
    private lateinit var usecase: GetPlayerLastActionsUseCaseImpl

    @Before
    fun setup() {
        usecase = GetPlayerLastActionsUseCaseImpl()
    }

    private fun executeAndAssert(
        phaseStateList: List<PhaseState>,
        expected: Map<PlayerId, BetPhaseActionState?>
    ) {
        // execute
        val actual = usecase.invoke(
            playerOrder = listOf(
                PlayerId("0"),
                PlayerId("1"),
                PlayerId("2"),
            ),
            phaseStateList = phaseStateList
        )
        // assert
        assertEquals(expected, actual)
    }

    @Test
    fun standby() {
        // prepare
        val phaseStateList = listOf(
            PhaseState.Standby(phaseId = 0L),
        )
        val expected = mapOf<PlayerId, BetPhaseActionState?>(
            PlayerId("0") to null,
            PlayerId("1") to null,
            PlayerId("2") to null
        )
        executeAndAssert(phaseStateList, expected)
    }

    @Test
    fun preFlop_allCall() {
        // prepare
        val phaseStateList = listOf(
            PhaseState.Standby(phaseId = 0L),
            PhaseState.PreFlop(
                phaseId = 0L,
                actionStateList = listOf<BetPhaseActionState>(
                    Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0),
                    Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0),
                    Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0),
                    Call(actionId = 3L, playerId = PlayerId("0"), betSize = 200.0),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseActionState?>(
            PlayerId("0") to Call(actionId = 3L, playerId = PlayerId("0"), betSize = 200.0),
            PlayerId("1") to Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0),
            PlayerId("2") to Call(actionId = 2L, playerId = PlayerId("2"), betSize = 200.0),
        )
        executeAndAssert(phaseStateList, expected)
    }

    @Test
    fun preFlop_allFold() {
        // prepare
        val phaseStateList = listOf(
            PhaseState.Standby(phaseId = 0L),
            PhaseState.PreFlop(
                phaseId = 0L,
                actionStateList = listOf(
                    Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0),
                    Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0),
                    Fold(actionId = 2L, playerId = PlayerId("2")),
                    Fold(actionId = 3L, playerId = PlayerId("0")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseActionState?>(
            PlayerId("0") to Fold(actionId = 3L, playerId = PlayerId("0")),
            PlayerId("1") to Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0),
            PlayerId("2") to Fold(actionId = 2L, playerId = PlayerId("2")),
        )
        executeAndAssert(phaseStateList, expected)
    }

    @Test
    fun preFlop_2AllIn_1Fold() {
        // prepare
        val phaseStateList = listOf(
            PhaseState.Standby(phaseId = 0L),
            PhaseState.PreFlop(
                phaseId = 0L,
                actionStateList = listOf(
                    Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0),
                    Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 200.0),
                    AllIn(actionId = 2L, playerId = PlayerId("2"), betSize = 1000.0),
                    AllIn(actionId = 3L, playerId = PlayerId("0"), betSize = 1500.0),
                    Fold(actionId = 4L, playerId = PlayerId("1")),
                )
            )
        )
        val expected = mapOf<PlayerId, BetPhaseActionState?>(
            PlayerId("0") to AllIn(actionId = 3L, playerId = PlayerId("0"), betSize = 1500.0),
            PlayerId("1") to Fold(actionId = 4L, playerId = PlayerId("1")),
            PlayerId("2") to AllIn(actionId = 2L, playerId = PlayerId("2"), betSize = 1000.0),
        )
        executeAndAssert(phaseStateList, expected)
    }
}