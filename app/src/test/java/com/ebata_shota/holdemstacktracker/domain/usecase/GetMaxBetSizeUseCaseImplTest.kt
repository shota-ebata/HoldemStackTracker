package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMaxBetSizeUseCaseImplTest {

    private lateinit var usecase: GetMaxBetSizeUseCaseImpl

    @Before
    fun setup() {
        usecase = GetMaxBetSizeUseCaseImpl()
    }

    @Test
    fun getMaxBetSize_empty() {
        val actual = usecase.invoke(
            actionStateList = emptyList()
        )
        val expected = 0.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_all_check() {
        val actual = usecase.invoke(
            actionStateList = listOf(
                BetPhaseActionState.Check(
                    actionId = 0L,
                    playerId = PlayerId("")
                ),
                BetPhaseActionState.Check(
                    actionId = 0L,
                    playerId = PlayerId("")
                )
            )
        )
        val expected = 0.0f
        assertEquals(expected, actual)
    }

    private fun createFullActions() = listOf(
        BetPhaseActionState.Blind(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 100.0f
        ),
        BetPhaseActionState.Blind(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 200.0f
        ),
        BetPhaseActionState.Call(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 200.0f
        ),
        BetPhaseActionState.Raise(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 400.0f
        ),
        BetPhaseActionState.Fold(
            actionId = 0L,
            playerId = PlayerId("")
        ),
        BetPhaseActionState.Skip(
            actionId = 0L,
            playerId = PlayerId("")
        ),
        BetPhaseActionState.AllIn(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 1000.0f
        ),
        BetPhaseActionState.AllIn(
            actionId = 0L,
            playerId = PlayerId(""),
            betSize = 900.0f
        )
    )

    @Test
    fun getMaxBetSize_to_SB() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(1)
        )
        val expected = 100.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_BB() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(2)
        )
        val expected = 200.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_Call() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(3)
        )
        val expected = 200.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_Raise() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(4)
        )
        val expected = 400.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_Fold() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(5)
        )
        val expected = 400.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_Skip() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(6)
        )
        val expected = 400.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_AllIn() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(7)
        )
        val expected = 1000.0f
        assertEquals(expected, actual)
    }

    @Test
    fun getMaxBetSize_to_AllIn_min() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(8)
        )
        val expected = 1000.0f
        assertEquals(expected, actual)
    }
}