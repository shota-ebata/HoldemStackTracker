package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PotSettlementInfo
import com.ebata_shota.holdemstacktracker.domain.model.TableId

interface SetPotSettlementInfoUseCase {

    suspend fun invoke(
        tableId: TableId,
        game: Game,
        potSettlementInfoList: List<PotSettlementInfo>,
    )
}