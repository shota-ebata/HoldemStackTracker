package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pod
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPodStateListUseCaseImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPodListUseCaseImplTest {
    private lateinit var useCase: GetPodStateListUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetPodStateListUseCaseImpl(dispatcher)
    }

    private fun executeAndAssert(
        pendingBetPerPlayer: Map<PlayerId, Double>,
        expectedList: List<Pod>
    ) {
        runTest(dispatcher) {
            // execute
            val updatedPodList: List<Pod> = useCase.invoke(
                podList = emptyList(),
                pendingBetPerPlayer = pendingBetPerPlayer
            )
            // テスト用にIDを0に上書き
            val actualList = updatedPodList.map { it.copy(id = 0L) }
            // assert
            actualList.zip(expectedList) { actual, expected ->
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun all_call() {
        val pendingBetPerPlayer: Map<PlayerId, Double> = mapOf(
            PlayerId("0") to 200.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 200.0,
        )
        val expectedList = listOf(
            Pod(
                id = 0L,
                podNumber = 0L,
                podSize = 600.0,
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
        val pendingBetPerPlayer: Map<PlayerId, Double> = mapOf(
            PlayerId("0") to 200.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 100.0,
        )
        val expectedList = listOf(
            Pod(
                id = 0L,
                podNumber = 0L,
                podSize = 300.0,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pod(
                id = 0L,
                podNumber = 1L,
                podSize = 200.0,
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
        val pendingBetPerPlayer: Map<PlayerId, Double> = mapOf(
            PlayerId("0") to 100.0,
            PlayerId("1") to 200.0,
            PlayerId("2") to 300.0,
        )
        val expectedList = listOf(
            Pod(
                id = 0L,
                podNumber = 0L,
                podSize = 300.0,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pod(
                id = 0L,
                podNumber = 1L,
                podSize = 200.0,
                involvedPlayerIds = listOf(
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pod(
                id = 0L,
                podNumber = 2L,
                podSize = 100.0,
                involvedPlayerIds = listOf(
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(pendingBetPerPlayer, expectedList)
    }
}