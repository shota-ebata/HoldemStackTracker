package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Table
import java.time.Instant

interface UpdateTableUseCase {
    /**
     * versionをインクリメントしつつ更新する
     * @param table: テーブル（更新日時は次の引数で上書きされる）
     * @param updateTime: 更新日時（先の引数tableの、updateTimeを上書きする）
     */
    suspend fun invoke(
        table: Table,
        updateTime: Instant = Instant.now(),
    )
}