package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.CurrentActionPlayerIdUseCaseImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CurrentActionPlayerIdUseCaseImplTest {

    private lateinit var useCase: CurrentActionPlayerIdUseCaseImpl

    private fun createPlayers(num: Int) = (0 until num).map {
        PlayerState(
            id = it.toLong(),
            name = "name_$it",
            stack = 200.0f,
            isLeaved = false
        )
    }

    @Before
    fun setup() {
        useCase = CurrentActionPlayerIdUseCaseImpl()
    }

    @Test
    fun default_model() {
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = emptyList(),
            basePlayerId = 0L,
            phaseStateList = emptyList()
        )
        assertNull(currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_0to1() {
        val players = createPlayers(2)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        assertEquals(1L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[1].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        assertEquals(2L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_1to0() {
        val players = createPlayers(2)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[1].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BTN_2to0() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[2].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
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
        assertEquals(2L, currentActionPlayerId)
    }

    @Test
    fun preflop_after_BB_2to0() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
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
        assertEquals(0L, currentActionPlayerId)
    }

    @Test
    fun preflop_2_after_BTN_0to1() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
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
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
            phaseStateList = listOf(
                getClosedPreFlop(players),
                PhaseState.Flop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        assertEquals(1L, currentActionPlayerId)
    }

    @Test
    fun flop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = useCase.getCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            basePlayerId = players[0].id,
            phaseStateList = listOf(
                getClosedPreFlop(players),
                PhaseState.Flop(
                    phaseId = 1L,
                    actionStateList = listOf(
                        ActionState.Check(
                            actionId = 0L,
                            playerId = players[1].id
                        )
                    )
                )
            )
        )
        assertEquals(2L, currentActionPlayerId)
    }
}