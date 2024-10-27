package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.CurrentActionPlayerIdUseCaseImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class CurrentActionPlayerIdUseCaseImplTest {

    private lateinit var model: TableState

    private lateinit var useCase: CurrentActionPlayerIdUseCaseImpl

    private fun createPlayers(num: Int) = (0 until num).map {
        PlayerState(
            id = it.toLong(),
            name = "name_$it",
            stack = 200.0f,
            isLeaved = false
        )
    }

    private fun createModel(
        players: List<PlayerState>,
        btnId: Long,
        phaseStateList: List<PhaseState> = listOf(
            PhaseState.PreFlop(
                phaseId = 0L,
                actionStateList = emptyList()
            )
        )
    ): TableState = model.copy(
        players = players,
        playerOrder = players.map { it.id },
        btnPlayerId = btnId,
        phaseStateList =phaseStateList
    )

    @Before
    fun setup() {
        useCase = CurrentActionPlayerIdUseCaseImpl()
        model = TableState(
            id = 0L,
            version = 0,
            name = "dummy",
            hostPlayerId = 0L,
            players = emptyList(),
            podInfoList = emptyList(),
            playerOrder = listOf(0L, 1L),
            btnPlayerId = 1L,
            phaseStateList = emptyList(),
            ruleStatus = RuleState.LingGame(
                sbSize = 100.0f,
                bbSize = 200.0f,
                betViewMode = BetViewMode.Number
            ),
            startTime = LocalDateTime.now()
        )
    }

    @Test
    fun default_model() {
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(model)
        assertNull(currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_0to1() {
        val players = createPlayers(2)
        val currentModel = createModel(
            players = players,
            btnId = players.first().id
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(1L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_1to2() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[1].id
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(2L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_1to0() {
        val players = createPlayers(2)
        val currentModel = createModel(
            players = players,
            btnId = players[1].id
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_2to0() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[2].id
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[0].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        ActionState.Blind(
                            actionId = 0L,
                            playerId = players[1].id,
                            betSize = 1.0f
                        )
                    )
                )
            )
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(2L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BB_2to0() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[0].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        ActionState.Blind(
                            actionId = 0L,
                            playerId = players[1].id,
                            betSize = 1.0f
                        ),
                        ActionState.Blind(
                            actionId = 1L,
                            playerId = players[2].id,
                            betSize = 2.0f
                        )
                    )
                )
            )
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_2_after_BTN_0to1() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[0].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        ActionState.Blind(
                            actionId = 0L,
                            playerId = players[1].id,
                            betSize = 1.0f
                        ),
                        ActionState.Blind(
                            actionId = 1L,
                            playerId = players[2].id,
                            betSize = 2.0f
                        ),
                        ActionState.Raise(
                            actionId = 2L,
                            playerId = players[0].id,
                            betSize = 5.0f
                        )
                    )
                )
            )
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(1L, currentActionPlayerId)
    }

    private fun getClosedPreFlop(players: List<PlayerState>) = PhaseState.PreFlop(
        phaseId = 0L,
        actionStateList = listOf(
            ActionState.Blind(
                actionId = 0L,
                playerId = players[1].id,
                betSize = 1.0f
            ),
            ActionState.Blind(
                actionId = 1L,
                playerId = players[2].id,
                betSize = 2.0f
            ),
            ActionState.Raise(
                actionId = 2L,
                playerId = players[0].id,
                betSize = 5.0f
            ),
            ActionState.Call(
                actionId = 3L,
                playerId = players[1].id,
                betSize = 5.0f
            ),
            ActionState.Call(
                actionId = 4L,
                playerId = players[2].id,
                betSize = 5.0f
            )
        )
    )

    @Test
    fun flop_after_BTN_0to1() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[0].id,
            phaseStateList = listOf(
                getClosedPreFlop(players),
                PhaseState.Flop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(1L, currentActionPlayerId)
    }

    @Test
    fun flop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentModel = createModel(
            players = players,
            btnId = players[0].id,
            phaseStateList = listOf(
                getClosedPreFlop(players),
                PhaseState.Flop(
                    phaseId = 0L,
                    actionStateList = listOf(
                        ActionState.Check(
                            actionId = 0L,
                            playerId = players[1].id
                        )
                    )
                )
            )
        )
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(currentModel)
        assertEquals(2L, currentActionPlayerId)
    }
}