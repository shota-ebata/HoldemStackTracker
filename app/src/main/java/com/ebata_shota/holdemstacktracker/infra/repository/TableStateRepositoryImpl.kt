package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableStateMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TableStateRepositoryImpl
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
) : TableStateRepository {

    private val tablesRef: DatabaseReference = firebaseDatabase.getReference(
        "tables"
    )

    private val _tableStateFlow = MutableSharedFlow<TableState>()
    override val tableStateFlow: Flow<TableState> = _tableStateFlow.asSharedFlow()

    override suspend fun createNewTable(
        tableId: TableId,
        tableName: String,
        ruleState: RuleState
    ) {
        withContext(ioDispatcher) {
            val uid = firebaseAuthRepository.uidFlow.first()
            val myPlayerId = PlayerId(uid)
            val myName = prefRepository.myName.first()
            val tableState = TableState(
                id = tableId,
                version = 0L,
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                name = tableName,
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
                startTime = 0L
            )
            setTableState(tableState)
        }
    }

    override fun startCollectTableStateFlow(tableId: TableId) {
        appCoroutineScope.launch {
            val flow = callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val tableMap: Map<*, *> = snapshot.value as Map<*, *>
                            val tableState = tableMapper.mapToTableState(tableId, tableMap)
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
            flow.collect(_tableStateFlow)
        }
    }

    override suspend fun setTableState(
        newTableState: TableState
    ) {
        val tableMap = tableMapper.toMap(newTableState)
        val tableRef = tablesRef.child(newTableState.id.value)
        tableRef.setValue(tableMap)
    }
}