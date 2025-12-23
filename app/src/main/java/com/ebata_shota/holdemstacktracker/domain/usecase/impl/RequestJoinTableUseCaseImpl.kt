package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.RequestJoinTableUseCase
import javax.inject.Inject

class RequestJoinTableUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
) : RequestJoinTableUseCase {

    // TODO: UT書きたい
    override suspend fun invoke(
        table: Table,
        myPlayerId: PlayerId,
        myName: String
    ) {
        if (table.playerOrder.any { it == myPlayerId}) {
            // すでにplayerOrderが存在しているならbaseも存在しているので
            // 着席扱いにする
            tableRepository.updateSeat(
                tableId = table.id,
                playerId = myPlayerId,
                isSeat = true
            )
        } else if (table.waitPlayerIds.none { it.value == myPlayerId }) {
            // playerOrderに参加していなくて
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