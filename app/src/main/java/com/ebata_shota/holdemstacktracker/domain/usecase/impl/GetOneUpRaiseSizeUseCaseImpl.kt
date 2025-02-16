package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getDigitCount
import com.ebata_shota.holdemstacktracker.domain.util.getMinNumberForDigits
import com.ebata_shota.holdemstacktracker.domain.util.roundUpToDigit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetOneUpRaiseSizeUseCaseImpl
@Inject
constructor(
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetOneUpRaiseSizeUseCase {

    override suspend fun invoke(
        currentRaiseSize: Int,
        game: Game,
    ): Int = withContext(dispatcher) {
        val currentDigitCount = getDigitCount(currentRaiseSize)
        val changeDigit = if (currentDigitCount - 1 > 0) {
            currentDigitCount - 1
        } else {
            currentDigitCount
        }

        val roundUpRaiseSize = roundUpToDigit(currentRaiseSize, changeDigit - 1)

        val nextRaiseSize = if (currentRaiseSize != roundUpRaiseSize) {
            // 現在のRaiseサイズと切り上げたサイズが一致しないなら
            // 端数を上げただけなのでそれを適用する
            roundUpRaiseSize
        } else {
            // 端数がないので、切り上げ
            currentRaiseSize + getMinNumberForDigits(changeDigit)
        }

        val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
        val player = game.players.find { it.id == myPlayerId }!!
        val stackSize = player.stack

        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLastPhaseAsBetPhase.invoke(game.phaseList).actionStateList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId
        )
        return@withContext if (nextRaiseSize > stackSize + myPendingBetSize) {
            stackSize
        } else {
            nextRaiseSize
        }
    }
}