package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase.AllInOpen
import com.ebata_shota.holdemstacktracker.domain.model.Phase.End
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Flop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PotSettlement
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.River
import com.ebata_shota.holdemstacktracker.domain.model.Phase.ShowDown
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Standby
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Turn
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetNextPhaseTypeUseCaseImplTest {
    private lateinit var useCase: GetNextPhaseUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetNextPhaseUseCaseImpl(
            getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
                getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                    dispatcher = dispatcher
                ),
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
        )
    }

    @Test
    fun getNextPhase_from_Standby() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    Standby
                )
            )
            assert(actual is PreFlop)
        }
    }

    @Test
    fun getNextPhase_from_AllInOpen() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    AllInOpen
                )
            )
            assert(actual is PotSettlement)
        }
    }

    @Test
    fun getNextPhase_from_ShowDown() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    ShowDown
                )
            )
            assert(actual is PotSettlement)
        }
    }

    @Test
    fun getNextPhase_from_PotSettlement() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PotSettlement
                )
            )
            assert(actual is End)
        }
    }

    @Test
    fun getNextPhase_from_End() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    End
                )
            )
            assert(actual is Standby)
        }
    }

    @Test
    fun getNextPhase_from_BetPhase_Active1() {
         runTest(dispatcher) {
             val actual = useCase.invoke(
                 playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                 phaseList = listOf(
                     PreFlop(
                         actionStateList = listOf(
                             BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100),
                             BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 100),
                             BetPhaseAction.Fold(playerId = PlayerId("2")),
                             BetPhaseAction.Fold(playerId = PlayerId("0")),
                         )
                     )
                 )
             )
             assert(actual is PotSettlement)
         }
    }

    @Test
    fun getNextPhase_from_BetPhase_2AllIn_1Fold() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PreFlop(
                        actionStateList = listOf(
                            BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100),
                            BetPhaseAction.Blind(playerId = PlayerId("1"), betSize = 100),
                            BetPhaseAction.Fold(playerId = PlayerId("2")),
                            BetPhaseAction.AllIn(playerId = PlayerId("0"), betSize = 1000),
                            BetPhaseAction.AllIn(playerId = PlayerId("1"), betSize = 1500),
                        )
                    )
                )
            )
            assert(actual is AllInOpen)
        }
    }

    @Test
    fun getNextPhase_from_PreFlop() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PreFlop(
                        actionStateList = listOf()
                    )
                )
            )
            assert(actual is Flop)
        }
    }

    @Test
    fun getNextPhase_from_Flop() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PreFlop(actionStateList = emptyList()),
                    Flop(actionStateList = emptyList())
                )
            )
            assert(actual is Turn)
        }
    }

    @Test
    fun getNextPhase_from_Turn() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PreFlop(actionStateList = emptyList()),
                    Flop(actionStateList = emptyList()),
                    Turn(actionStateList = emptyList())
                )
            )
            assert(actual is River)
        }
    }

    @Test
    fun getNextPhase_from_River() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(PlayerId("0"), PlayerId("1"), PlayerId("2")),
                phaseList = listOf(
                    PreFlop(actionStateList = emptyList()),
                    Flop(actionStateList = emptyList()),
                    Turn(actionStateList = emptyList()),
                    River(actionStateList = emptyList())
                )
            )
            assert(actual is ShowDown)
        }
    }
}