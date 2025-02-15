package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.model.PotId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextAutoActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GetNextAutoActionUseCaseImplTest {

    private lateinit var useCase: GetNextAutoActionUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetNextAutoActionUseCaseImpl(
            getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
                getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                    dispatcher = dispatcher,
                ),
                dispatcher = dispatcher,
            ),
            randomIdRepository = mockk {
                every { generateRandomId() } returns "hoge"
            },
            dispatcher = dispatcher,
        )
    }


    /**
     * BTN: 200
     * SB : 100
     * BB : 200
     *
     * PreFlop：SB（AllIn）、他（Call）
     * Flop：BB（Fold）
     *
     * このとき、AutoActionはとくになし
     */
    @Test
    fun flop_BB_Fold_is_null() {

        val playerId = PlayerId("")
        val rule = Rule.RingGame(
            sbSize = 1,
            bbSize = 2,
            defaultStack = 200,
        )
        val playerOrder = listOf(
            PlayerId("BTN"),
            PlayerId("SB"),
            PlayerId("BB"),
        )
        val game: Game = createDummyGame().copy(
            players = setOf(
                GamePlayer(
                    id = PlayerId("BTN"),
                    stack = 98,
                ),
                GamePlayer(
                    id = PlayerId("SB"),
                    stack = 0,
                ),
                GamePlayer(
                    id = PlayerId("BB"),
                    stack = 98,
                ),
            ),
            potList = listOf(
                Pot(
                    id = PotId("0"),
                    potNumber = 0,
                    potSize = 300,
                    involvedPlayerIds = listOf(
                        PlayerId("BTN"),
                        PlayerId("SB"),
                        PlayerId("BB"),
                    ),
                    isClosed = true
                ),
                Pot(
                    id = PotId("0"),
                    potNumber = 1,
                    potSize = 4,
                    involvedPlayerIds = listOf(
                        PlayerId("BTN"),
                        PlayerId("BB"),
                    ),
                    isClosed = false
                ),
            ),
            phaseList = listOf(
                Phase.PreFlop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.Blind(
                            actionId = ActionId(""),
                            playerId = PlayerId("SB"),
                            betSize = 1
                        ),
                        BetPhaseAction.Blind(
                            actionId = ActionId(""),
                            playerId = PlayerId("BB"),
                            betSize = 2
                        ),
                        BetPhaseAction.Call(
                            actionId = ActionId(""),
                            playerId = PlayerId("BTN"),
                            betSize = 2
                        ),
                        BetPhaseAction.AllIn(
                            actionId = ActionId(""),
                            playerId = PlayerId("SB"),
                            betSize = 100
                        ),
                        BetPhaseAction.Call(
                            actionId = ActionId(""),
                            playerId = PlayerId("BB"),
                            betSize = 100
                        ),
                        BetPhaseAction.Call(
                            actionId = ActionId(""),
                            playerId = PlayerId("BTN"),
                            betSize = 100
                        ),
                    )
                ),
                Phase.Flop(
                    phaseId = PhaseId(""),
                    actionStateList = listOf(
                        BetPhaseAction.AllInSkip(actionId = ActionId(""), playerId = PlayerId("SB")),
                        BetPhaseAction.Fold(actionId = ActionId(""), playerId = PlayerId("BB")),
                    )
                )
            )
        )
        runTest(dispatcher) {
            val nextBetPhaseAction = useCase.invoke(
                playerId = playerId,
                rule = rule,
                playerOrder = playerOrder,
                game = game
            )
            assertThat(nextBetPhaseAction).isNull()
        }
    }

}