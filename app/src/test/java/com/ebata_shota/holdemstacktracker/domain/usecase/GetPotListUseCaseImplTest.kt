package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPotStateListUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPotListUseCaseImplTest {
    private lateinit var useCase: GetPotStateListUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetPotStateListUseCaseImpl(dispatcher)
    }

    private fun executeAndAssert(
        pendingBetPerPlayer: Map<PlayerId, Int>,
        expectedList: List<Pot>
    ) {
        runTest(dispatcher) {
            // execute
            val updatedPotList: List<Pot> = useCase.invoke(
                potList = emptyList(),
                pendingBetPerPlayer = pendingBetPerPlayer
            )
            // テスト用にIDを0に上書き
            val actualList = updatedPotList.map { it.copy(id = 0L) }
            // assert
            actualList.zip(expectedList) { actual, expected ->
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun all_call() {
        val pendingBetPerPlayer: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 200,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val expectedList = listOf(
            Pot(
                id = 0L,
                potNumber = 0L,
                potSize = 600,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(pendingBetPerPlayer, expectedList)
    }

    @Test
    fun all_in_2() {
        val pendingBetPerPlayer: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 200,
            PlayerId("1") to 200,
            PlayerId("2") to 100,
        )
        val expectedList = listOf(
            Pot(
                id = 0L,
                potNumber = 0L,
                potSize = 300,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pot(
                id = 0L,
                potNumber = 1L,
                potSize = 200,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(pendingBetPerPlayer, expectedList)
    }

    @Test
    fun all_in_3() {
        val pendingBetPerPlayer: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 100,
            PlayerId("1") to 200,
            PlayerId("2") to 300,
        )
        val expectedList = listOf(
            Pot(
                id = 0L,
                potNumber = 0L,
                potSize = 300,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pot(
                id = 0L,
                potNumber = 1L,
                potSize = 200,
                involvedPlayerIds = listOf(
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pot(
                id = 0L,
                potNumber = 2L,
                potSize = 100,
                involvedPlayerIds = listOf(
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(pendingBetPerPlayer, expectedList)
    }
}