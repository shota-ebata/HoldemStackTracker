package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.AddBetPhaseActionInToGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetActionablePlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastPhaseAsBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNotFoldPlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPotListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredInPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.infra.repository.RandomIdRepositoryImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AddBetPhaseActionInToGameUseCaseImplTest {
    private lateinit var useCase: AddBetPhaseActionInToGameUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        val getPlayerLastActions = GetPlayerLastActionsUseCaseImpl(
            getPlayerLastActionUseCase = GetPlayerLastActionUseCaseImpl(
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher,
        )
        val getMaxBetSize = GetMaxBetSizeUseCaseImpl(
            dispatcher = dispatcher,
        )
        val getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
            getMaxBetSize = getMaxBetSize,
            dispatcher = dispatcher,
        )
        val getLastPhaseAsBetPhase = GetLastPhaseAsBetPhaseUseCaseImpl(
            dispatcher = dispatcher,
        )
        val getActionablePlayerIds = GetActionablePlayerIdsUseCaseImpl(
            getPlayerLastActions = getPlayerLastActions,
            dispatcher = dispatcher,
        )
        val getNotFoldPlayerIds = GetNotFoldPlayerIdsUseCaseImpl(
            getPlayerLastActions = getPlayerLastActions,
            dispatcher = dispatcher,
        )
        val randomIdRepository = RandomIdRepositoryImpl()
        useCase = AddBetPhaseActionInToGameUseCaseImpl(
            isActionRequiredInPhase = IsActionRequiredInPhaseUseCaseImpl(
                getMaxBetSize = getMaxBetSize,
                getPendingBetSize = GetPendingBetSizeUseCaseImpl(
                    getPendingBetPerPlayer = getPendingBetPerPlayer,
                    dispatcher = dispatcher,
                ),
                getNotFoldPlayerIds = getNotFoldPlayerIds,
                getActionablePlayerIds = getActionablePlayerIds,
                dispatcher = dispatcher,
            ),
            getLastPhaseAsBetPhase = getLastPhaseAsBetPhase,
            getNextPlayerStack = GetNextPlayerStackUseCaseImpl(
                getLastPhaseAsBetPhase = getLastPhaseAsBetPhase,
                getPendingBetPerPlayer = getPendingBetPerPlayer,
                getNextPlayerStateList = GetNextGamePlayerStateListUseCaseImpl(
                    dispatcher = dispatcher,
                ),
                dispatcher = dispatcher,
            ),
            getNotFoldPlayerIds = getNotFoldPlayerIds,
            getActionablePlayerIds = getActionablePlayerIds,
            getPendingBetPerPlayer = getPendingBetPerPlayer,
            getPotStateList = GetPotListUseCaseImpl(
                randomIdRepository = randomIdRepository,
                dispatcher = dispatcher,
            ),
            randomIdRepository = randomIdRepository,
            dispatcher = dispatcher,
        )
    }

    @Test
    fun fold_BB() {
        runTest(dispatcher) {
            val game = Game(
                gameId = GameId("game1"),
                version = 1,
                tableId = TableId("table1"),
                appVersion = 1,
                btnPlayerId = PlayerId("BTN"),
                players = listOf(
                    GamePlayer(
                        id = PlayerId("BTN"),
                        stack = 198
                    ),
                    GamePlayer(
                        id = PlayerId("SB"),
                        stack = 199
                    ),
                    GamePlayer(
                        id = PlayerId("BB"),
                        stack = 198
                    ),
                ),
                potList = listOf(),
                phaseList = listOf(
                    Phase.Standby(
                        phaseId = PhaseId("phase1"),
                    ),
                    Phase.PreFlop(
                        phaseId = PhaseId("phase2"),
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
                        )
                    )
                ),
                updateTime = Instant.now(),
            )
            val betPhaseAction = BetPhaseAction.Fold(
                actionId = ActionId("action4"),
                playerId = PlayerId("BB"),
            )
            // Act
            val actual = useCase.invoke(
                currentGame = game,
                betPhaseAction = betPhaseAction
            )
            // Assert
            assertEquals(198, actual.players.first { it.id == PlayerId("BTN") }.stack)
            assertEquals(1, actual.potList.size)
            assertEquals(3, actual.potList[0].involvedPlayerIds.size)
        }
    }
}