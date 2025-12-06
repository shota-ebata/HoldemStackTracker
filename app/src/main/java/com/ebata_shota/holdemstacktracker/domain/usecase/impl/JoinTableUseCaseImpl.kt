package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import javax.inject.Inject

class JoinTableUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : JoinTableUseCase {

    override suspend fun invoke(
        table: Table,
        myPlayerId: PlayerId,
        myName: String
    ) {
        if (
            table.basePlayers.none { it.id == myPlayerId }
            && table.waitPlayerIds.none { it.value == myPlayerId }
        ) {
            // playerOrderにもwaitにも自分がいないなら
            // waitに自分を追加
            // playerOrderへの追加はホストにやってもらう
            tableRepository.addBasePlayer(
                tableId = table.id,
                playerId = myPlayerId,
                name = myName,
            )
        }
    }
}