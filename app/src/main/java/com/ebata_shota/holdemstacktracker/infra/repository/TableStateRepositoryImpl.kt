package com.ebata_shota.holdemstacktracker.infra.repository

import android.util.Log
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RealtimeDatabaseRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.GameMapper
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
    private val realtimeDatabaseRepository: RealtimeDatabaseRepository,
    private val tableMapper: TableStateMapper,
    private val gameMapper: GameMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
) : TableStateRepository {


    private val flow: Flow<String> = flow {
        while (true) {
            delay(1000L)
            val value = LocalDateTime.now().toString()
            Log.d("hoge", "call flow emit($value)")
            emit(value)
        }
    }

    private val gameStateFlow: Flow<GameState> =  flow {

    }

    init {
        appCoroutineScope.launch {

//            flow.collect {
//
//            }
        }
    }

    override fun getTableStateFlow(tableId: Long): Flow<GameState> {
        return gameStateFlow
    }

    /**
     * TableStateを更新してFirebaseRealtimeDatabaseに送る
     *
     * @param newGameState 新しいTableState
     */
    override suspend fun setNewGameState(
        tableId: TableId,
        newGameState: GameState
    ) {
        val hashMap = gameMapper.mapToHashMap(newGameState)
        realtimeDatabaseRepository.setGameHashMap(
            tableId = tableId,
            gameHashMap = hashMap
        )
    }
}