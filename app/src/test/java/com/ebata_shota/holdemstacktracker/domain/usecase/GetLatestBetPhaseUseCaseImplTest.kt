package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class GetLatestBetPhaseUseCaseImplTest {
    private lateinit var usecase: GetLatestBetPhaseUseCaseImpl

    @Before
    fun setup() {
        usecase = GetLatestBetPhaseUseCaseImpl()
    }

    @Test
    fun getLatestBet_Standby() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L)
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGameState = gameState)
        }
    }

    @Test
    fun getLatestBet_PreFlop() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList())
            )
        )
        val phase = usecase.invoke(latestGameState = gameState)
        assert(phase is PhaseState.PreFlop)
    }

    @Test
    fun getLatestBet_Flop() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList())
            )
        )
        val phase = usecase.invoke(latestGameState = gameState)
        assert(phase is PhaseState.Flop)
    }

    @Test
    fun getLatestBet_Turn() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList()),
                PhaseState.Turn(phaseId = 3L, actionStateList = emptyList()),
            )
        )
        val phase = usecase.invoke(latestGameState = gameState)
        assert(phase is PhaseState.Turn)
    }

    @Test
    fun getLatestBet_River() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList()),
                PhaseState.Turn(phaseId = 3L, actionStateList = emptyList()),
                PhaseState.River(phaseId = 4L, actionStateList = emptyList()),
            )
        )
        val phase = usecase.invoke(latestGameState = gameState)
        assert(phase is PhaseState.River)
    }

    @Test
    fun getLatestBet_ShowDown() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList()),
                PhaseState.Turn(phaseId = 3L, actionStateList = emptyList()),
                PhaseState.River(phaseId = 4L, actionStateList = emptyList()),
                PhaseState.ShowDown(phaseId = 5L),
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGameState = gameState)
        }
    }

    @Test
    fun getLatestBet_PotSettlement() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList()),
                PhaseState.Turn(phaseId = 3L, actionStateList = emptyList()),
                PhaseState.River(phaseId = 4L, actionStateList = emptyList()),
                PhaseState.ShowDown(phaseId = 5L),
                PhaseState.PotSettlement(phaseId = 6L),
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGameState = gameState)
        }
    }

    @Test
    fun getLatestBet_End() {
        val gameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.Standby(phaseId = 0L),
                PhaseState.PreFlop(phaseId = 1L, actionStateList = emptyList()),
                PhaseState.Flop(phaseId = 2L, actionStateList = emptyList()),
                PhaseState.Turn(phaseId = 3L, actionStateList = emptyList()),
                PhaseState.River(phaseId = 4L, actionStateList = emptyList()),
                PhaseState.ShowDown(phaseId = 5L),
                PhaseState.PotSettlement(phaseId = 5L),
                PhaseState.End(phaseId = 6L),
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGameState = gameState)
        }
    }
}