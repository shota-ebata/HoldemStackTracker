package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.AllInOpen
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.End
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Flop
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.PotSettlement
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.River
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.ShowDown
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Standby
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.Turn
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import org.junit.Before
import org.junit.Test

class GetNextPhaseUseCaseImplTest {
    private lateinit var usecase: GetNextPhaseUseCaseImpl

    @Before
    fun setup() {
        usecase = GetNextPhaseUseCaseImpl(
            getPlayerLastActions = GetPlayerLastActionsUseCaseImpl()
        )
    }

    @Test
    fun getNextPhase_from_Standby() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                Standby(phaseId = 0L)
            )
        )
        assert(actual is PreFlop)
    }

    @Test
    fun getNextPhase_from_AllInOpen() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                AllInOpen(phaseId = 0L)
            )
        )
        assert(actual is PotSettlement)
    }

    @Test
    fun getNextPhase_from_ShowDown() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                ShowDown(phaseId = 0L)
            )
        )
        assert(actual is PotSettlement)
    }

    @Test
    fun getNextPhase_from_PotSettlement() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PotSettlement(phaseId = 0L)
            )
        )
        assert(actual is End)
    }

    @Test
    fun getNextPhase_from_End() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                End(phaseId = 0L)
            )
        )
        assert(actual is Standby)
    }

    @Test
    fun getNextPhase_from_BetPhase_Active1() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
                        BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 100.0f),
                        BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
                        BetPhaseActionState.Fold(actionId = 3L, playerId = PlayerId("0")),
                    )
                )
            )
        )
        assert(actual is PotSettlement)
    }

    @Test
    fun getNextPhase_from_BetPhase_2AllIn_1Fold() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f),
                        BetPhaseActionState.Blind(actionId = 1L, playerId = PlayerId("1"), betSize = 100.0f),
                        BetPhaseActionState.Fold(actionId = 2L, playerId = PlayerId("2")),
                        BetPhaseActionState.AllIn(actionId = 3L, playerId = PlayerId("0"), betSize = 1000.0f),
                        BetPhaseActionState.AllIn(actionId = 4L, playerId = PlayerId("1"), betSize = 1500.0f),
                    )
                )
            )
        )
        assert(actual is AllInOpen)
    }

    @Test
    fun getNextPhase_from_PreFlop() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf()
                )
            )
        )
        assert(actual is Flop)
    }

    @Test
    fun getNextPhase_from_Flop() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(phaseId = 0L, actionStateList = emptyList()),
                Flop(phaseId = 0L, actionStateList = emptyList())
            )
        )
        assert(actual is Turn)
    }

    @Test
    fun getNextPhase_from_Turn() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(phaseId = 0L, actionStateList = emptyList()),
                Flop(phaseId = 0L, actionStateList = emptyList()),
                Turn(phaseId = 0L, actionStateList = emptyList())
            )
        )
        assert(actual is River)
    }

    @Test
    fun getNextPhase_from_River() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(phaseId = 0L, actionStateList = emptyList()),
                Flop(phaseId = 0L, actionStateList = emptyList()),
                Turn(phaseId = 0L, actionStateList = emptyList()),
                River(phaseId = 0L, actionStateList = emptyList())
            )
        )
        assert(actual is ShowDown)
    }
}