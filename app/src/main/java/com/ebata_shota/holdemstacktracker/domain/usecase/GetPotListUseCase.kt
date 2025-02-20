package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot
import com.ebata_shota.holdemstacktracker.domain.model.PotAndRemainingBet

interface GetPotListUseCase {
    /**
     * ベット状況をポットに反映して返却
     * @param potList ポット状況
     * @param pendingBetPerPlayerWithoutZero ポットに入っていないBetが残っているプレイヤーのベット状況
     */
    suspend fun invoke(
        updatedPlayers: List<GamePlayer>,
        potList: List<Pot>,
        pendingBetPerPlayerWithoutZero: Map<PlayerId, Int>,
        activePlayerIds: List<PlayerId>,
    ): PotAndRemainingBet
}