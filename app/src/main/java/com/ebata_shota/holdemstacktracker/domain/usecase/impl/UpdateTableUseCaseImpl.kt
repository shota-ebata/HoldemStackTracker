package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.UpdateTableUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

/**
 * 更新時間とテーブルバージョンを更新してテーブルを更新する
 */
class UpdateTableUseCaseImpl
@Inject
constructor(
    private val tableRepository: TableRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : UpdateTableUseCase {

    override suspend fun invoke(
        table: Table,
        updateTime: Instant,
    ) = withContext(dispatcher) {
        tableRepository.sendTable(
            table.copy(
                updateTime = updateTime,
                version = table.version + 1
            )
        )
    }
}