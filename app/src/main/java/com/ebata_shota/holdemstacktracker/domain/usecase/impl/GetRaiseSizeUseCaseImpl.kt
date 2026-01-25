package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSliderUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeUseCase
import javax.inject.Inject

class GetRaiseSizeUseCaseImpl
@Inject
constructor(
    private val getPendingBetSize: GetPendingBetSizeUseCase,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getRaiseSizeByStackSliderUseCase: GetRaiseSizeByStackSliderUseCase,
) : GetRaiseSizeUseCase {
    override suspend fun invoke(
        game: Game,
        myPlayerId: PlayerId,
        minRaiseSize: Int,
        sliderPosition: Float,
    ): Int {
        val player = game.players.find { it.id == myPlayerId }!!
        val stackSize = player.stack
        val myPendingBetSize = getPendingBetSize.invoke(
            actionList = getLastPhaseAsBetPhase.invoke(game.phaseList).actionStateList,
            playerOrder = game.playerOrder,
            playerId = myPlayerId
        )
        val raiseSize: Int = getRaiseSizeByStackSliderUseCase.invoke(
            stackSize = stackSize,
            minRaiseSize = minRaiseSize,
            myPendingBetSize = myPendingBetSize,
            sliderPosition = sliderPosition,
        )
        return raiseSize
    }
}