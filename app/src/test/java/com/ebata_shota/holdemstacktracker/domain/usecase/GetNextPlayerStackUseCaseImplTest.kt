package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.createDummyGame
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNextPlayerStackUseCaseImplTest {
    private lateinit var useCase: GetNextPlayerStackUseCaseImpl
    private val firebaseAuthRepository: FirebaseAuthRepository = mockk()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // TODO: hilt
        useCase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = GetLatestBetPhaseUseCaseImpl(StandardTestDispatcher()),
            getPendingBetPerPlayer = GetPendingBetPerPlayerUseCaseImpl(
                getMaxBetSize = GetMaxBetSizeUseCaseImpl(
                    dispatcher = dispatcher
                ),
                dispatcher = dispatcher
            ),
            getNextPlayerStateList = GetNextGamePlayerStateListUseCaseImpl(
                firebaseAuthRepository = firebaseAuthRepository,
                dispatcher = dispatcher
            ),
            dispatcher = dispatcher
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

        useCase = GetNextPlayerStackUseCaseImpl(
            getLatestBetPhase = getLatestBetPhaseUseCase,
            getPendingBetPerPlayer = getPendingBetPerPlayerUseCase,
            getNextPlayerStateList = getNextGamePlayerStateListUseCase,
            dispatcher = dispatcher,
        )

        val mockNextPlayerStateListResult = setOf<GamePlayer>(
            GamePlayer(
                id = PlayerId("0"),
                stack = 1000,
                isLeaved = false
            ),
        )
        coEvery { getNextGamePlayerStateListUseCase.invoke(any(), any(), any()) } returns mockNextPlayerStateListResult

        val mockLatestBetPhaseResult = Phase.PreFlop(
            phaseId = PhaseId(""),
            actionStateList = listOf<BetPhaseAction>()
        )
        coEvery { getLatestBetPhaseUseCase.invoke(any()) } returns mockLatestBetPhaseResult

        val mockPendingBetPerPlayerResult = mapOf<PlayerId, Int>(
            PlayerId("0") to 100,
        )
        coEvery {
            getPendingBetPerPlayerUseCase.invoke(
                any(),
                any()
            )
        } returns mockPendingBetPerPlayerResult

        val latestGame = createDummyGame(
            phaseList = listOf(
                Phase.PreFlop(phaseId = PhaseId(""), actionStateList = emptyList())
            )
        )
        val action = BetPhaseAction.Blind(actionId = ActionId(""), playerId = PlayerId("0"), betSize = 100)
        val playerOrder = listOf(PlayerId("0"), PlayerId("1"))

        runTest(dispatcher) {
            // execute
            val actual = useCase.invoke(
                latestGame = latestGame,
                action = action,
                playerOrder = playerOrder
            )

            // assert
            coVerify(exactly = 1) {
                getLatestBetPhaseUseCase.invoke(latestGame)
            }
            coVerify(exactly = 1) {
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