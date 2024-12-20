package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextPlayerStackUseCaseImplTest {
    private lateinit var usecase: GetNextPlayerStackUseCaseImpl
    private val firebaseAuthRepository: FirebaseAuthRepository = mockk()

    @Before
    fun setup() {
        usecase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl(),
            getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                getMaxBetSize = GetMaxBetSizeUseCaseImpl()
            ),
            getNextPlayerStateList = GetNextGamePlayerStateListUseCaseImpl(
                firebaseAuthRepository = firebaseAuthRepository
            )
        )
    }

    /**
     *  想定通りmockが実行されること
     *  mockから取得した値を想定通りreturnすること
     */
    @Test
    fun call_mock() {
        val getLatestBetPhaseUseCase: GetLatestBetPhaseUseCaseImpl = mockk()
        val getPendingBetPerPlayerUseCase: GetPendingBetPerPlayerUseCaseImpl = mockk()
        val getNextGamePlayerStateListUseCase: GetNextGamePlayerStateListUseCaseImpl = mockk()

        usecase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseUseCase,
            getPendingBetPerPlayer = getPendingBetPerPlayerUseCase,
            getNextPlayerStateList = getNextGamePlayerStateListUseCase
        )

        val mockNextPlayerStateListResult = setOf<GamePlayer>(
            GamePlayer(
                id = PlayerId("0"),
                stack = 1000.0,
                isLeaved = false
            ),
        )
        coEvery { getNextGamePlayerStateListUseCase.invoke(any(), any(), any()) } returns mockNextPlayerStateListResult

        val mockLatestBetPhaseResult = Phase.PreFlop(
            actionStateList = listOf<BetPhaseAction>()
        )
        every { getLatestBetPhaseUseCase.invoke(any()) } returns mockLatestBetPhaseResult

        val mockPendingBetPerPlayerResult = mapOf<PlayerId, Double>(
            PlayerId("0") to 100.0,
        )
        every { getPendingBetPerPlayerUseCase.invoke(any(), any()) } returns mockPendingBetPerPlayerResult

        val latestGame = createDummyGame(
            phaseList = listOf(
                Phase.PreFlop(actionStateList = emptyList())
            )
        )
        val action = BetPhaseAction.Blind(playerId = PlayerId("0"), betSize = 100.0)
        val playerOrder = listOf(PlayerId("0"), PlayerId("1"))

        runTest {
            // execute
            val actual =  usecase.invoke(
                latestGame = latestGame,
                action = action,
                playerOrder = playerOrder
            )

            // assert
            verify(exactly = 1) {
                getLatestBetPhaseUseCase.invoke(latestGame)
            }
            verify(exactly = 1) {
                getPendingBetPerPlayerUseCase.invoke(
                    playerOrder = playerOrder,
                    actionStateList = mockLatestBetPhaseResult.actionStateList
                )
            }
            coVerify(exactly = 1) {
                getNextGamePlayerStateListUseCase.invoke(
                    pendingBetPerPlayer = mockPendingBetPerPlayerResult,
                    players = latestGame.players,
                    action = action,
                )
            }
            assertEquals(mockNextPlayerStateListResult, actual)
        }
    }
}