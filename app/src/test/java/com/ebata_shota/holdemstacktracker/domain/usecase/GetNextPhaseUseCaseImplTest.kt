package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import org.junit.Before
import org.junit.Test

class GetNextPhaseUseCaseImplTest {
    private lateinit var usecase: GetNextPhaseUseCaseImpl

    @Before
    fun setup() {
        usecase = GetNextPhaseUseCaseImpl()
    }

    @Test
    fun getNextPhase_from_PreFlop() {
        val actual = usecase.invoke(
            currentPhase = PhaseState.PreFlop(phaseId = 0L, actionStateList = emptyList())
        )
        assert(actual is PhaseState.Flop)
    }

    @Test
    fun getNextPhase_from_Flop() {
        val actual = usecase.invoke(
            currentPhase = PhaseState.Flop(phaseId = 0L, actionStateList = emptyList())
        )
        assert(actual is PhaseState.Turn)
    }

    @Test
    fun getNextPhase_from_Turn() {
        val actual = usecase.invoke(
            currentPhase = PhaseState.Turn(phaseId = 0L, actionStateList = emptyList())
        )
        assert(actual is PhaseState.River)
    }

    @Test
    fun getNextPhase_from_River() {
        val actual = usecase.invoke(
            currentPhase = PhaseState.River(phaseId = 0L, actionStateList = emptyList())
        )
        assert(actual is PhaseState.ShowDown)
    }
}