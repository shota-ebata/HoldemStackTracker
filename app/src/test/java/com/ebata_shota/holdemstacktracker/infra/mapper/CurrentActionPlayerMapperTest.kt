package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CurrentActionPlayerMapperTest {
    private lateinit var mapper: CurrentActionPlayerMapper

    private fun createPlayers(num: Int) = (0 until num).map {
        PlayerState(
            id = PlayerId(it.toString()),
            name = "name_$it",
            stack = 200.0f,
            isLeaved = false
        )
    }

    @Before
    fun setup() {
        mapper = CurrentActionPlayerMapper()
    }

    @Test
    fun default_model() {
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = emptyList(),
            btnPlayerId = PlayerId("0"),
            phaseStateList = emptyList()
        )
        assertNull(currentActionPlayerId)
    }

    @Test
    fun preFlop_after_BTN_0to1() {
        val players = createPlayers(2)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val expected =  PlayerId("1")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_after_BTN_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[1].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val expected =  PlayerId("2")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_after_BTN_1to0() {
        val players = createPlayers(2)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[1].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val expected =  PlayerId("0")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_after_BTN_2to0() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[2].id,
            phaseStateList = listOf(
                PhaseState.PreFlop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val expected =  PlayerId("0")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
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
        val expected =  PlayerId("2")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_after_BB_2to0() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
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
        val expected =  PlayerId("0")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun preFlop_2_after_BTN_0to1() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
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
        val expected =  PlayerId("1")
        assertEquals(expected, currentActionPlayerId)
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
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
            phaseStateList = listOf(
                getClosedPreFlop(players),
                PhaseState.Flop(
                    phaseId = 0L,
                    actionStateList = emptyList()
                )
            )
        )
        val expected =  PlayerId("1")
        assertEquals(expected, currentActionPlayerId)
    }

    @Test
    fun flop_after_SB_1to2() {
        val players = createPlayers(3)
        val currentActionPlayerId = mapper.mapCurrentActionPlayerId(
            playerOrder = players.map { it.id },
            btnPlayerId = players[0].id,
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
        val expected =  PlayerId("2")
        assertEquals(expected, currentActionPlayerId)
    }
}