package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
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
        val expected = 0.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_all_check() {
        val actual = usecase.invoke(
            actionStateList = listOf(
                BetPhaseAction.Check(
                    playerId = PlayerId("")
                ),
                BetPhaseAction.Check(
                    playerId = PlayerId("")
                )
            )
        )
        val expected = 0.0
        assertEquals(expected, actual, 0.0)
    }

    private fun createFullActions() = listOf(
        BetPhaseAction.Blind(
            playerId = PlayerId(""),
            betSize = 100.0
        ),
        BetPhaseAction.Blind(
            playerId = PlayerId(""),
            betSize = 200.0
        ),
        BetPhaseAction.Call(
            playerId = PlayerId(""),
            betSize = 200.0
        ),
        BetPhaseAction.Raise(
            playerId = PlayerId(""),
            betSize = 400.0
        ),
        BetPhaseAction.Fold(
            playerId = PlayerId("")
        ),
        BetPhaseAction.FoldSkip(
            playerId = PlayerId("")
        ),
        BetPhaseAction.AllIn(
            playerId = PlayerId(""),
            betSize = 1000.0
        ),
        BetPhaseAction.AllIn(
            playerId = PlayerId(""),
            betSize = 900.0
        )
    )

    @Test
    fun getMaxBetSize_to_SB() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(1)
        )
        val expected = 100.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_BB() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(2)
        )
        val expected = 200.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_Call() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(3)
        )
        val expected = 200.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_Raise() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(4)
        )
        val expected = 400.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_Fold() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(5)
        )
        val expected = 400.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_Skip() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(6)
        )
        val expected = 400.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_AllIn() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(7)
        )
        val expected = 1000.0
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun getMaxBetSize_to_AllIn_min() {
        val actual = usecase.invoke(
            actionStateList = createFullActions().take(8)
        )
        val expected = 1000.0
        assertEquals(expected, actual, 0.0)
    }
}