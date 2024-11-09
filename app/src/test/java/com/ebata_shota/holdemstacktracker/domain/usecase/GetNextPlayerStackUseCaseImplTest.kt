package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGameState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
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
    private val prefRepository: PrefRepository = mockk()

    @Before
    fun setup() {
        usecase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl(),
            getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                getMaxBetSize = GetMaxBetSizeUseCaseImpl()
            ),
            getNextPlayerStateList = GetNextGamePlayerStateListUseCaseImpl(
                prefRepository = prefRepository
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

        val mockNextPlayerStateListResult = listOf<GamePlayerState>(
            GamePlayerState(
                id = PlayerId("0"),
                stack = 1000.0,
                isLeaved = false
            ),
        )
        coEvery { getNextGamePlayerStateListUseCase.invoke(any(), any(), any()) } returns mockNextPlayerStateListResult

        val mockLatestBetPhaseResult = PhaseState.PreFlop(
            actionStateList = listOf<BetPhaseActionState>()
        )
        every { getLatestBetPhaseUseCase.invoke(any()) } returns mockLatestBetPhaseResult

        val mockPendingBetPerPlayerResult = mapOf<PlayerId, Double>(
            PlayerId("0") to 100.0,
        )
        every { getPendingBetPerPlayerUseCase.invoke(any(), any()) } returns mockPendingBetPerPlayerResult

        val latestGameState = createDummyGameState(
            phaseStateList = listOf(
                PhaseState.PreFlop(actionStateList = emptyList())
            )
        )
        val action = BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0)

        runTest {
            // execute
            val actual =  usecase.invoke(
                latestGameState = latestGameState,
                action = action
            )

            // assert
            verify(exactly = 1) {
                getLatestBetPhaseUseCase.invoke(latestGameState)
            }
            verify(exactly = 1) {
                getPendingBetPerPlayerUseCase.invoke(
                    playerOrder = latestGameState.playerOrder,
                    actionStateList = mockLatestBetPhaseResult.actionStateList
                )
            }
            coVerify(exactly = 1) {
                getNextGamePlayerStateListUseCase.invoke(
                    pendingBetPerPlayer = mockPendingBetPerPlayerResult,
                    players = latestGameState.players,
                    action = action,
                )
            }
            assertEquals(mockNextPlayerStateListResult, actual)
        }
    }
}