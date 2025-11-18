package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNotFoldPlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNotFoldPlayerIdsUseCaseImplTest {
    private lateinit var useCase: GetNotFoldPlayerIdsUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetNotFoldPlayerIdsUseCaseImpl(
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
    fun test() {
        runTest(dispatcher) {
            val actual = useCase.invoke(
                playerOrder = listOf(
                    PlayerId("SB"),
                    PlayerId("BB"),
                    PlayerId("BTN"),
                ),
                phaseList = listOf(
                    Phase.PreFlop(
                        phaseId = PhaseId("phase1"),
                        actionStateList = listOf(
                            BetPhaseAction.Blind(
                                actionId = ActionId("action0"),
                                playerId = PlayerId("SB"),
                                betSize = 1,
                            ),
                            BetPhaseAction.Blind(
                                actionId = ActionId("action1"),
                                playerId = PlayerId("BB"),
                                betSize = 2,
                            ),
                            BetPhaseAction.Call(
                                actionId = ActionId("action2"),
                                playerId = PlayerId("BTN"),
                                betSize = 2,
                            ),
                            BetPhaseAction.Fold(
                                actionId = ActionId("action3"),
                                playerId = PlayerId("SB"),
                            ),
                            BetPhaseAction.Fold(
                                actionId = ActionId("action4"),
                                playerId = PlayerId("BB"),
                            ),
                        )
                    )
                ),
            )
            assertEquals(listOf(PlayerId("BTN")), actual)
        }
    }
}