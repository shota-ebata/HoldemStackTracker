package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class GetLatestBetPhaseTypeUseCaseImplTest {
    private lateinit var useCase: GetLatestBetPhaseUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetLatestBetPhaseUseCaseImpl(dispatcher)
    }

    @Test
    fun getLatestBet_Standby() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby
            )
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(latestGame = game)
            }
        }
    }

    @Test
    fun getLatestBet_PreFlop() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList())
            )
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(latestGame = game)
            assert(phase is Phase.PreFlop)
        }
    }

    @Test
    fun getLatestBet_Flop() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList())
            )
        )

        runTest(dispatcher) {
            val phase = useCase.invoke(latestGame = game)
            assert(phase is Phase.Flop)
        }
    }

    @Test
    fun getLatestBet_Turn() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList()),
                Phase.Turn(actionStateList = emptyList()),
            )
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(latestGame = game)
            assert(phase is Phase.Turn)
        }
    }

    @Test
    fun getLatestBet_River() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList()),
                Phase.Turn(actionStateList = emptyList()),
                Phase.River(actionStateList = emptyList()),
            )
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(latestGame = game)
            assert(phase is Phase.River)
        }
    }

    @Test
    fun getLatestBet_ShowDown() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList()),
                Phase.Turn(actionStateList = emptyList()),
                Phase.River(actionStateList = emptyList()),
                Phase.ShowDown,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(latestGame = game)
            }
        }
    }

    @Test
    fun getLatestBet_PotSettlement() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList()),
                Phase.Turn(actionStateList = emptyList()),
                Phase.River(actionStateList = emptyList()),
                Phase.ShowDown,
                Phase.PotSettlement,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(latestGame = game)
            }
        }
    }

    @Test
    fun getLatestBet_End() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby,
                Phase.PreFlop(actionStateList = emptyList()),
                Phase.Flop(actionStateList = emptyList()),
                Phase.Turn(actionStateList = emptyList()),
                Phase.River(actionStateList = emptyList()),
                Phase.ShowDown,
                Phase.PotSettlement,
                Phase.End,
            )
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(latestGame = game)
            }
        }
    }
}