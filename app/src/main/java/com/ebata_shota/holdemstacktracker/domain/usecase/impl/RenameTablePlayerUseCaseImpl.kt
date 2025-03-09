package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import android.util.Log
import com.ebata_shota.holdemstacktracker.domain.extension.indexOfFirstOrNull
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import javax.inject.Inject

class RenameTablePlayerUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository
) : RenameTablePlayerUseCase {

    /**
     * 必要ならテーブル内に保存しているプレイヤー名を更新する
     */
    override suspend fun invoke(
        table: Table,
        playerId: PlayerId,
        name: String
    ) {
        val indexOfBasePlayers = table.basePlayers.indexOfFirstOrNull { it.id == playerId }
        if (indexOfBasePlayers != null) {
            val basePlayer = table.basePlayers[indexOfBasePlayers]
            if (basePlayer.name != name) {
                tableRepository.renameTableBasePlayer(
                    tableId = table.id,
                    indexOfBasePlayers = indexOfBasePlayers.toLong(),
                    playerId = playerId,
                    name = name
                )
            }
        }
    }
}