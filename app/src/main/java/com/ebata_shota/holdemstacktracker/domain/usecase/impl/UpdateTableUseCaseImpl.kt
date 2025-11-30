package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject


class UpdateTableUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : UpdateTableUseCase {

    /**
     * versionをインクリメントしつつ更新する
     * @param table: テーブル（更新日時は次の引数で上書きされる）
     * @param updateTime: 更新日時（先の引数tableの、updateTimeを上書きする）
     */
    override suspend fun invoke(
        table: Table,
        updateTime: Instant,
    ) = withContext(dispatcher) {
        val current = tableRepository.tableStateFlow.value?.getOrNull()
        if (table != current) {
            tableRepository.sendTable(
                table.copy(
                    updateTime = updateTime,
                    version = table.version + 1
                )
            )
        }
    }
}