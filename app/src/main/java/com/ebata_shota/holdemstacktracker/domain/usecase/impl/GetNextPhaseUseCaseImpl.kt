package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.End
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Flop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PotSettlement
import com.ebata_shota.holdemstacktracker.domain.model.Phase.PreFlop
import com.ebata_shota.holdemstacktracker.domain.model.Phase.River
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Standby
import com.ebata_shota.holdemstacktracker.domain.model.Phase.Turn
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PhaseStatus
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextPhaseUseCaseImpl
@Inject
constructor(
    private val randomIdRepository: RandomIdRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetNextPhaseUseCase {

    /**
     * BetPhaseのときは
     * PhaseStatus.Active
     * 以外の時に呼ばれる想定
     */
    override suspend fun invoke(
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>,
    ): Phase = withContext(dispatcher) {
        return@withContext when (val latestPhase: Phase? = phaseList.lastOrNull()) {
            is Standby -> PreFlop(
                phaseId = PhaseId(randomIdRepository.generateRandomId()),
                actionStateList = emptyList(),
                phaseStatus = PhaseStatus.Active
            )

            is BetPhase -> getNextPhaseStateFromBetPhase(
                latestPhase = latestPhase
            )

            is PotSettlement -> throw IllegalStateException("SetPotSettlementInfoUseCase で Endに変更するので あり得ない想定")

            is End -> Standby(
                phaseId = PhaseId(randomIdRepository.generateRandomId())
            )
            null -> {
                // 基本無いはずだが
                Standby(phaseId = PhaseId(randomIdRepository.generateRandomId()))
            }
        }
    }

    private suspend fun getNextPhaseStateFromBetPhase(
        latestPhase: BetPhase
    ): Phase {
        // その他の場合は次のべットフェーズへ
        // (フェーズのアクションがすべて終わっている前提で、このメソッドを呼び出している想定）
        return when (latestPhase.phaseStatus) {
            PhaseStatus.Active -> {
                throw IllegalStateException("PhaseStatus.Active is not supported.")
            }

            // 閉じているなら次のフェーズへ
            PhaseStatus.Close -> when (latestPhase) {
                is PreFlop -> {
                    Flop(
                        phaseId = PhaseId(randomIdRepository.generateRandomId()),
                        actionStateList = emptyList(),
                    )
                }

                is Flop -> {
                    Turn(
                        phaseId = PhaseId(randomIdRepository.generateRandomId()),
                        actionStateList = emptyList(),
                    )
                }

                is Turn -> {
                    River(
                        phaseId = PhaseId(randomIdRepository.generateRandomId()),
                        actionStateList = emptyList(),
                    )
                }

                is River -> {
                    PotSettlement(
                        phaseId = PhaseId(randomIdRepository.generateRandomId()),
                    )
                }
            }

            PhaseStatus.AllInClose -> {
                // AllInの場合はPotSettlement
                PotSettlement(
                    phaseId = PhaseId(randomIdRepository.generateRandomId())
                )
            }
        }
    }
}