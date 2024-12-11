package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class GetLatestBetPhaseTypeUseCaseImplTest {
    private lateinit var usecase: GetLatestBetPhaseUseCaseImpl

    @Before
    fun setup() {
        usecase = GetLatestBetPhaseUseCaseImpl()
    }

    @Test
    fun getLatestBet_Standby() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGame = game)
        }
    }

    @Test
    fun getLatestBet_PreFlop() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList())
            )
        )
        val phase = usecase.invoke(latestGame = game)
        assert(phase is PhaseState.PreFlop)
    }

    @Test
    fun getLatestBet_Flop() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList())
            )
        )
        val phase = usecase.invoke(latestGame = game)
        assert(phase is PhaseState.Flop)
    }

    @Test
    fun getLatestBet_Turn() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList()),
                PhaseState.Turn(actionStateList = emptyList()),
            )
        )
        val phase = usecase.invoke(latestGame = game)
        assert(phase is PhaseState.Turn)
    }

    @Test
    fun getLatestBet_River() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList()),
                PhaseState.Turn(actionStateList = emptyList()),
                PhaseState.River(actionStateList = emptyList()),
            )
        )
        val phase = usecase.invoke(latestGame = game)
        assert(phase is PhaseState.River)
    }

    @Test
    fun getLatestBet_ShowDown() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList()),
                PhaseState.Turn(actionStateList = emptyList()),
                PhaseState.River(actionStateList = emptyList()),
                PhaseState.ShowDown,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGame = game)
        }
    }

    @Test
    fun getLatestBet_PotSettlement() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList()),
                PhaseState.Turn(actionStateList = emptyList()),
                PhaseState.River(actionStateList = emptyList()),
                PhaseState.ShowDown,
                PhaseState.PotSettlement,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGame = game)
        }
    }

    @Test
    fun getLatestBet_End() {
        val game = createDummyGame(
            phaseStateList = listOf(
                PhaseState.Standby,
                PhaseState.PreFlop(actionStateList = emptyList()),
                PhaseState.Flop(actionStateList = emptyList()),
                PhaseState.Turn(actionStateList = emptyList()),
                PhaseState.River(actionStateList = emptyList()),
                PhaseState.ShowDown,
                PhaseState.PotSettlement,
                PhaseState.End,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            usecase.invoke(latestGame = game)
        }
    }
}