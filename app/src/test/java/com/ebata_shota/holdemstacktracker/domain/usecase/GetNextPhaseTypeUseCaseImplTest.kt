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

class GetNextPhaseTypeUseCaseImplTest {
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
                Standby
            )
        )
        assert(actual is PreFlop)
    }

    @Test
    fun getNextPhase_from_AllInOpen() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                AllInOpen
            )
        )
        assert(actual is PotSettlement)
    }

    @Test
    fun getNextPhase_from_ShowDown() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                ShowDown
            )
        )
        assert(actual is PotSettlement)
    }

    @Test
    fun getNextPhase_from_PotSettlement() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PotSettlement
            )
        )
        assert(actual is End)
    }

    @Test
    fun getNextPhase_from_End() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                End
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
                    actionStateList = listOf(
                        BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
                        BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 100.0),
                        BetPhaseActionState.Fold(playerId = PlayerId("2")),
                        BetPhaseActionState.Fold(playerId = PlayerId("0")),
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
                    actionStateList = listOf(
                        BetPhaseActionState.Blind(playerId = PlayerId("0"), betSize = 100.0),
                        BetPhaseActionState.Blind(playerId = PlayerId("1"), betSize = 100.0),
                        BetPhaseActionState.Fold(playerId = PlayerId("2")),
                        BetPhaseActionState.AllIn(playerId = PlayerId("0"), betSize = 1000.0),
                        BetPhaseActionState.AllIn(playerId = PlayerId("1"), betSize = 1500.0),
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
                PreFlop(actionStateList = emptyList()),
                Flop(actionStateList = emptyList())
            )
        )
        assert(actual is Turn)
    }

    @Test
    fun getNextPhase_from_Turn() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(actionStateList = emptyList()),
                Flop(actionStateList = emptyList()),
                Turn(actionStateList = emptyList())
            )
        )
        assert(actual is River)
    }

    @Test
    fun getNextPhase_from_River() {
        val actual = usecase.invoke(
            playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
            phaseStateList = listOf(
                PreFlop(actionStateList = emptyList()),
                Flop(actionStateList = emptyList()),
                Turn(actionStateList = emptyList()),
                River(actionStateList = emptyList())
            )
        )
        assert(actual is ShowDown)
    }
}