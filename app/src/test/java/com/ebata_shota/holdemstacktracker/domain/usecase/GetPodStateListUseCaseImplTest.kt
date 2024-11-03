package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PodState
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPodStateListUseCaseImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPodStateListUseCaseImplTest {
    private lateinit var usecase: GetPodStateListUseCaseImpl

    @Before
    fun setup() {
        usecase = GetPodStateListUseCaseImpl()
    }

    private fun executeAndAssert(pendingBetPerPlayer: Map<PlayerId, Float>, expectedList: List<PodState>) {
        // execute
        val updatedPodStateList: List<PodState> = usecase.invoke(
            podStateList = emptyList(),
            pendingBetPerPlayer = pendingBetPerPlayer
        )
        // テスト用にIDを0に上書き
        val actualList = updatedPodStateList.map { it.copy(id = 0L) }
        // assert
        actualList.zip(expectedList) { actual, expected ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun all_call() {
        val pendingBetPerPlayer: Map<PlayerId, Float> = mapOf(
            PlayerId("0") to 200.0f,
            PlayerId("1") to 200.0f,
            PlayerId("2") to 200.0f,
        )
        val expectedList = listOf(
            PodState(
                id = 0L,
                podNumber = 0,
                podSize = 600.0f,
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
        val pendingBetPerPlayer: Map<PlayerId, Float> = mapOf(
            PlayerId("0") to 200.0f,
            PlayerId("1") to 200.0f,
            PlayerId("2") to 100.0f,
        )
        val expectedList = listOf(
            PodState(
                id = 0L,
                podNumber = 0,
                podSize = 300.0f,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            PodState(
                id = 0L,
                podNumber = 1,
                podSize = 200.0f,
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
        val pendingBetPerPlayer: Map<PlayerId, Float> = mapOf(
            PlayerId("0") to 100.0f,
            PlayerId("1") to 200.0f,
            PlayerId("2") to 300.0f,
        )
        val expectedList = listOf(
            PodState(
                id = 0L,
                podNumber = 0,
                podSize = 300.0f,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            PodState(
                id = 0L,
                podNumber = 1,
                podSize = 200.0f,
                involvedPlayerIds = listOf(
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            PodState(
                id = 0L,
                podNumber = 2,
                podSize = 100.0f,
                involvedPlayerIds = listOf(
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(pendingBetPerPlayer, expectedList)
    }
}