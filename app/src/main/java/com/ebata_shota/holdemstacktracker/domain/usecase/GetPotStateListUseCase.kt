package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pot

interface GetPotStateListUseCase {
    /**
     * ベット状況をポットに反映して返却
     * @param potList ポット状況
     * @param pendingBetPerPlayer ポットに入っていない別途が残っているプレイヤーのベット状況
     */
    suspend fun invoke(
        potList: List<Pot>,
        pendingBetPerPlayer: Map<PlayerId, Int>,
        activePlayerIds: List<PlayerId>,
    ): List<Pot>
}