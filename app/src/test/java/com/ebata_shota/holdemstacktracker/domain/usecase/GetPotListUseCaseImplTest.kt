package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.model.PotAndRemainingBet
import com.ebata_shota.holdemstacktracker.domain.model.PotId
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPotListUseCaseImpl
import com.ebata_shota.holdemstacktracker.infra.repository.RandomIdRepositoryImpl
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GetPotListUseCaseImplTest {
    private lateinit var useCase: GetPotListUseCaseImpl

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        useCase = GetPotListUseCaseImpl(
            randomIdRepository = RandomIdRepositoryImpl(),
            dispatcher = dispatcher
        )
    }

    private fun executeAndAssert(
        updatedPlayers: List<GamePlayer>,
        pendingBetPerPlayerWithoutZero: Map<PlayerId, Int>,
        activePlayerIds: List<PlayerId>,
        potList: List<Pot> = emptyList(),
        expected: PotAndRemainingBet,
    ) {
        runTest(dispatcher) {
            // execute
            val potAndRemainingBet = useCase.invoke(
                updatedPlayers = updatedPlayers,
                potList = potList,
                pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
                activePlayerIds = activePlayerIds
            )
            // テスト用にIDを0に上書き
            val actual = potAndRemainingBet.copy(
                potList = potAndRemainingBet.potList.map { it.copy(id = PotId("0")) }
            )
            // assert
            assertThat(actual).isEqualTo(expected)
        }
    }

    /**
     * 3人全員Call
     * 全員スタック残
     */
    @Test
    fun all_call() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 100,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 100,
            ),
            GamePlayer(
                id = PlayerId("2"),
                stack = 100,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 200,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 600,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    /**
     * 1人AllIn
     * 他Call
     */
    @Test
    fun all_in() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 100,
            ),
            GamePlayer(
                id = PlayerId("2"),
                stack = 100,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 200,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 600,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    /**
     * 2人AllIn
     * 1人Call
     */
    @Test
    fun all_in_2_call_1() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("2"),
                stack = 100,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 200,
            PlayerId("1") to 200,
            PlayerId("2") to 200,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 600,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    /**
     * 3人AllIn（差分あり）
     */
    @Test
    fun all_in_3() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("2"),
                stack = 0,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 100,
            PlayerId("1") to 200,
            PlayerId("2") to 300,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
            PlayerId("1"),
            PlayerId("2"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 300,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pot(
                id = PotId("0"),
                potNumber = 1,
                potSize = 200,
                involvedPlayerIds = listOf(
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = mapOf(
                    PlayerId("2") to 100
                )
            ),
        )
    }

    @Test
    fun fold() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 100,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 100,
            ),
            GamePlayer(
                id = PlayerId("2"),
                stack = 100,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 400,
            PlayerId("1") to 200,
            PlayerId("2") to 400,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
            PlayerId("2"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 600,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                    PlayerId("2"),
                ),
                isClosed = true
            ),
            Pot(
                id = PotId("0"),
                potNumber = 1,
                potSize = 400,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("2"),
                ),
                isClosed = false
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    @Test
    fun all_in_() {
        val updatedPlayers = listOf(
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
        )
        val potList = listOf(
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
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("BTN") to 2,
            PlayerId("BB") to 2,
        )
        val activePlayerIds = listOf(
            PlayerId("BTN"),
            PlayerId("SB"),
            PlayerId("BB"),
        )
        val expectedList = listOf(
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
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            potList = potList,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    @Test
    fun all_in_a() {
        val updatedPlayers = listOf(
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
        )
        val potList = listOf(
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
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = emptyMap()
        val activePlayerIds = listOf(
            PlayerId("BTN"),
            PlayerId("SB"),
            PlayerId("BB"),
        )
        val expectedList = listOf(
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
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            potList = potList,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = emptyMap()
            ),
        )
    }

    /**
     * 1人AllIn
     * 1人Fold
     */
    @Test
    fun all_in_1_fold_1() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 100,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 101,
            PlayerId("1") to 2,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 4,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                ),
                isClosed = true
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = mapOf(
                    PlayerId("0") to 99
                )
            ),
        )
    }

    /**
     * 1人AllIn
     * 1人AllIn
     */
    @Test
    fun all_in_2() {
        val updatedPlayers = listOf(
            GamePlayer(
                id = PlayerId("0"),
                stack = 0,
            ),
            GamePlayer(
                id = PlayerId("1"),
                stack = 0,
            ),
        )
        val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int> = mapOf(
            PlayerId("0") to 101,
            PlayerId("1") to 100,
        )
        val activePlayerIds = listOf(
            PlayerId("0"),
        )
        val expectedList = listOf(
            Pot(
                id = PotId("0"),
                potNumber = 0,
                potSize = 200,
                involvedPlayerIds = listOf(
                    PlayerId("0"),
                    PlayerId("1"),
                ),
                isClosed = true
            ),
        )
        executeAndAssert(
            updatedPlayers = updatedPlayers,
            pendingBetPerPlayerWithoutZero = pendingBetPerPlayerWithoutZero,
            activePlayerIds = activePlayerIds,
            expected = PotAndRemainingBet(
                potList = expectedList,
                pendingBetPerPlayerWithoutZero = mapOf(
                    PlayerId("0") to 1
                )
            ),
        )
    }
}