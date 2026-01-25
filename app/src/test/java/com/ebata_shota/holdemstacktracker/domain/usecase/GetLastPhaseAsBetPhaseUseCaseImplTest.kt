package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.GameResult
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastPhaseAsBetPhaseUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class GetLastPhaseAsBetPhaseUseCaseImplTest {
    private lateinit var useCase: GetLastPhaseAsBetPhaseUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetLastPhaseAsBetPhaseUseCaseImpl(dispatcher)
    }

    @Test
    fun getLatestBet_Standby() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId(""))
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(phaseList = phaseList)
            }
        }
    }

    @Test
    fun getLatestBet_PreFlop() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList()),
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(phaseList = phaseList)
            assert(phase is Phase.PreFlop)
        }
    }

    @Test
    fun getLatestBet_Flop() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Flop(phaseId = PhaseId(""), actionStateList = emptyList()),
        )

        runTest(dispatcher) {
            val phase = useCase.invoke(phaseList = phaseList)
            assert(phase is Phase.Flop)
        }
    }

    @Test
    fun getLatestBet_Turn() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Flop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Turn(phaseId = PhaseId(""), actionStateList = emptyList()),
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(phaseList = phaseList)
            assert(phase is Phase.Turn)
        }
    }

    @Test
    fun getLatestBet_River() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Flop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Turn(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.River(phaseId = PhaseId(""), actionStateList = emptyList()),
        )
        runTest(dispatcher) {
            val phase = useCase.invoke(phaseList = phaseList)
            assert(phase is Phase.River)
        }
    }

    @Test
    fun getLatestBet_PotSettlement() {
        val game = createDummyGame(
            phaseList = listOf(
                Phase.Standby(phaseId = PhaseId("")),
                Phase.PreFlop(
                    phaseId = PhaseId(""),
                    actionStateList = emptyList(),
                ),
                Phase.Flop(phaseId = PhaseId(""), actionStateList = emptyList()),
                Phase.Turn(phaseId = PhaseId(""), actionStateList = emptyList()),
                Phase.River(
                    phaseId = PhaseId(""),
                    actionStateList = emptyList(),
                ),
                Phase.PotSettlement(phaseId = PhaseId("")),
            )
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(phaseList = game.phaseList)
            }
        }
    }

    @Test
    fun getLatestBet_End() {
        val phaseList = listOf(
            Phase.Standby(phaseId = PhaseId("")),
            Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Flop(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.Turn(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.River(phaseId = PhaseId(""), actionStateList = emptyList()),
            Phase.PotSettlement(phaseId = PhaseId("")),
            Phase.End(phaseId = PhaseId(""), GameResult(emptyList())),
        )
        assertThrows(IllegalStateException::class.java) {
            runTest(dispatcher) {
                useCase.invoke(phaseList = phaseList)
            }
        }
    }
}