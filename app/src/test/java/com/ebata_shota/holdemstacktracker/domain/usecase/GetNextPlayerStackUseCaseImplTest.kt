package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyTableState
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStateListUseCaseImpl
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
            getNextPlayerStateList = GetNextPlayerStateListUseCaseImpl(
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
        val getNextPlayerStateListUseCase: GetNextPlayerStateListUseCaseImpl = mockk()

        usecase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseUseCase,
            getPendingBetPerPlayer = getPendingBetPerPlayerUseCase,
            getNextPlayerStateList = getNextPlayerStateListUseCase
        )

        val mockNextPlayerStateListResult = listOf<PlayerState>(
            PlayerState(
                id = PlayerId("0"),
                name = "0",
                stack = 1000.0f,
                isLeaved = false
            ),
        )
        coEvery { getNextPlayerStateListUseCase.invoke(any(), any(), any()) } returns mockNextPlayerStateListResult

        val mockLatestBetPhaseResult = PhaseState.PreFlop(
            phaseId = 0L,
            actionStateList = listOf<BetPhaseActionState>()
        )
        every { getLatestBetPhaseUseCase.invoke(any()) } returns mockLatestBetPhaseResult

        val mockPendingBetPerPlayerResult = mapOf<PlayerId, Float>(
            PlayerId("0") to 100.0f,
        )
        every { getPendingBetPerPlayerUseCase.invoke(any(), any()) } returns mockPendingBetPerPlayerResult

        val latestTableState = createDummyTableState(
            phaseStateList = listOf(
                PhaseState.PreFlop(phaseId = 0L, actionStateList = emptyList())
            )
        )
        val action = BetPhaseActionState.Blind(actionId = 0L, playerId = PlayerId("0"), betSize = 100.0f)

        runTest {
            // execute
            val actual =  usecase.invoke(
                latestTableState = latestTableState,
                action = action
            )

            // assert
            verify(exactly = 1) {
                getLatestBetPhaseUseCase.invoke(latestTableState)
            }
            verify(exactly = 1) {
                getPendingBetPerPlayerUseCase.invoke(
                    playerOrder = latestTableState.playerOrder,
                    actionStateList = mockLatestBetPhaseResult.actionStateList
                )
            }
            coVerify(exactly = 1) {
                getNextPlayerStateListUseCase.invoke(
                    pendingBetPerPlayer = mockPendingBetPerPlayerResult,
                    players = latestTableState.players,
                    action = action,
                )
            }
            assertEquals(mockNextPlayerStateListResult, actual)
        }
    }
}