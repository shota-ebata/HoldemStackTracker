package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import javax.inject.Inject


// FIXME: プレイヤー名が異なるときの処理 が入るので名前変えたほうがいいかも？
class JoinTableUseCaseImpl
@Inject
constructor(
    private val updateTableUseCase: UpdateTableUseCase,
) : JoinTableUseCase {
    override suspend fun invoke(
        table: Table,
        myPlayerId: PlayerId,
        myName: String
    ) {
        if (
            table.playerOrder.none { it == myPlayerId }
            && table.waitPlayerIds.none { it == myPlayerId }
        ) {
            // playerOrderにもwaitにも自分がいないなら
            // waitに自分を追加
            // playerOrderへの追加はホストにやってもらう
            val newWaitPlayerIds = table.waitPlayerIds + myPlayerId
            var newBasePlayers = table.basePlayers
            if (table.basePlayers.none { it.id == myPlayerId }) {
                // basePlayersに自分がいないなら追加
                newBasePlayers = table.basePlayers + PlayerBase(
                    id = myPlayerId,
                    name = myName,
                    stack = table.rule.defaultStack,
                    isLeaved = false,
                )
            }
            val newTable = table.copy(
                basePlayers = newBasePlayers,
                waitPlayerIds = newWaitPlayerIds
            )
            if (newTable != table) {
                updateTableUseCase.invoke(newTable)
            }
        }
    }
}