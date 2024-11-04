package com.ebata_shota.holdemstacktracker.infra.repository

import android.util.Log
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableStateMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class TableStateRepositoryImpl
@Inject
constructor(
    private val prefRepository: PrefRepository,
    private val mapper: TableStateMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope
) : TableStateRepository {


    private val flow: Flow<String> = flow {
        while (true) {
            delay(1000L)
            val value = LocalDateTime.now().toString()
            Log.d("hoge", "call flow emit($value)")
            emit(value)
        }
    }

    private val tableStateFlow: Flow<TableState> =  flow {

    }

    init {
        appCoroutineScope.launch {

            flow.collect {

            }
        }
    }

    override fun getTableStateFlow(tableId: Long): Flow<TableState> {
        return tableStateFlow
    }

    /**
     * TableStateを更新してFirebaseRealtimeDatabaseに送る
     *
     * @param newTableState 新しいTableState
     */
    override suspend fun setTableState(newTableState: TableState) {
        TODO("変換して送る")
    }
}