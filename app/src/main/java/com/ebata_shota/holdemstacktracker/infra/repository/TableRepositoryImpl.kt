package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableStateMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TableRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val prefRepository: PrefRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val tableMapper: TableStateMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @CoroutineDispatcherIO
    private val ioDispatcher: CoroutineDispatcher
) : TableRepository {

    private val tablesRef: DatabaseReference = firebaseDatabase.getReference(
        "tables"
    )

    private val _tableFlow = MutableSharedFlow<Table>()
    override val tableFlow: SharedFlow<Table> = _tableFlow.asSharedFlow()

    override suspend fun createNewTable(
        tableId: TableId,
        ruleState: RuleState
    ) {
        withContext(ioDispatcher) {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val myName = prefRepository.myName.first()!!
            val tableCreateTime = System.currentTimeMillis()
            val table = Table(
                id = tableId,
                version = 0L,
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                hostPlayerId = myPlayerId,
                ruleState = ruleState,
                playerOrder = listOf(myPlayerId),
                btnPlayerId = myPlayerId,
                basePlayers = listOf(
                    PlayerBaseState(
                        id = myPlayerId,
                        name = myName,
                        stack = ruleState.defaultStack
                    )
                ),
                waitPlayers = emptyList(),
                tableStatus = TableStatus.STANDBY,
                startTime = 0L,
                tableCreateTime = tableCreateTime,
                updateTime = tableCreateTime
            )
            sendTable(table)
        }
    }

    private var collectTableJob: Job? = null

    override fun startCollectTableFlow(tableId: TableId) {
        stopCollectTableFlow()
        collectTableJob = appCoroutineScope.launch {
            val flow = callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val tableMap: Map<*, *> = snapshot.value as Map<*, *>
                            val tableState = tableMapper.mapToTableState(tableId, tableMap)
                            // TODO: JoinしたテーブルをDBに保存したい
                            trySend(tableState)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Failed to read game data: ${error.message}")
                    }
                }
                tablesRef.child(tableId.value).addValueEventListener(listener)

                awaitClose {
                    tablesRef.child(tableId.value).removeEventListener(listener)
                }
            }
            flow.collect(_tableFlow)
        }
    }

    override fun stopCollectTableFlow() {
        collectTableJob?.cancel()
    }

    override suspend fun sendTable(
        table: Table
    ) {
        val tableMap = tableMapper.toMap(table)
        val tableRef = tablesRef.child(table.id.value)
        tableRef.setValue(tableMap)
    }
}