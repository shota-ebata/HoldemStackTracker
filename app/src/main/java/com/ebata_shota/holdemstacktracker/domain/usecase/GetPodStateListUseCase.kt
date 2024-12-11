package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Pod

interface GetPodStateListUseCase {
    /**
     * ベット状況をポッドに反映して返却
     * @param podList ポッド状況
     * @param pendingBetPerPlayer ポッドに入っていない別途が残っているプレイヤーのベット状況
     */
    fun invoke(
        podList: List<Pod>,
        pendingBetPerPlayer: Map<PlayerId, Double>
    ): List<Pod>
}