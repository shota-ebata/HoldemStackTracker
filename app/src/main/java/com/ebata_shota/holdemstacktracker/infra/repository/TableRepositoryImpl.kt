package com.ebata_shota.holdemstacktracker.infra.repository

import android.util.Log
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundTableException
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.OnDisconnect
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class TableRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val prefRepository: PrefRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val tableMapper: TableMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @CoroutineDispatcherIO
    private val ioDispatcher: CoroutineDispatcher,
) : TableRepository {

    @Suppress("KotlinConstantConditions")
    private val tablesRef: DatabaseReference = firebaseDatabase.getReference(
        if (BuildConfig.BUILD_TYPE == "debug") {
            "debug_tables"
        } else {
            "tables"
        }
    )

    @Suppress("KotlinConstantConditions")
    private val tableConnectionRef: DatabaseReference = firebaseDatabase.getReference(
        if (BuildConfig.BUILD_TYPE == "debug") {
            "debug_table_connections"
        } else {
            "table_connections"
        }
    )

    private var myTableConnectionRef: DatabaseReference? = null
    private var myTableConnectionRefOnDisconnect: OnDisconnect? = null

    private val _tableStateFlow = MutableStateFlow<Result<Table>?>(null)
    override val tableStateFlow: StateFlow<Result<Table>?> = _tableStateFlow.asStateFlow()

    override suspend fun createNewTable(
        tableId: TableId,
        rule: Rule
    ) {
        withContext(ioDispatcher) {
            val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
            val myName = prefRepository.myName.first()!!
            val tableCreateTime = Instant.now()
            val table = Table(
                id = tableId,
                version = 0L,
                hostAppVersionCode = BuildConfig.VERSION_CODE,
                hostPlayerId = myPlayerId,
                rule = rule,
                playerOrder = listOf(myPlayerId),
                btnPlayerId = myPlayerId,
                potManagerPlayerId = myPlayerId,
                basePlayers = listOf(
                    PlayerBase(
                        id = myPlayerId,
                        name = myName,
                        stack = rule.defaultStack,
                        isLeaved = false,
                    )
                ),
                waitPlayerIds = emptyList(),
                connectionPlayerIds = emptyList(),
                tableStatus = TableStatus.PREPARING,
                currentGameId = null,
                startTime = null,
                tableCreateTime = tableCreateTime,
                updateTime = tableCreateTime
            )
            sendTable(table)
        }
    }

    private var collectTableJob: Job? = null
    private var tableConnectionJob: Job? = null

    override var currentTableId: TableId? = null
        private set

    override fun startCollectTableFlow(tableId: TableId) {
        if (currentTableId == tableId) {
            // 同じテーブルだった場合はすでに監視中なので無視
            return
        }
        stopCollectTableFlow()
        currentTableId = tableId
        collectTableJob = appCoroutineScope.launch {
            startCurrentTableConnectionIfNeed(tableId)

            launch {
                combine(
                    firebaseDatabaseTableFlow(tableId),
                    firebaseDatabaseTableConnectionFlow(tableId)
                ) { tableSnapshot: DataSnapshot, tableConnectionSnapshot: DataSnapshot ->
                    if (tableSnapshot.exists() && tableConnectionSnapshot.exists()) {
                        val connectionPlayerIds: List<PlayerId> = tableConnectionSnapshot.children
                            .map { it.key!! }
                            .map { PlayerId(it) }
                        val tableMap: Map<*, *> = tableSnapshot.value as Map<*, *>
                        val tableState = tableMapper.mapToTableState(
                            tableId = tableId,
                            tableMap = tableMap,
                            connectionPlayerIds = connectionPlayerIds
                        )
                        _tableStateFlow.emit(Result.success(tableState))
                    } else {
                        // FIXME: 例外の種類を豊富にしたい
                        _tableStateFlow.emit(Result.failure(NotFoundTableException()))
                    }
                }.collect()
            }
        }
    }

    override fun stopCollectTableFlow() {
        myTableConnectionRefOnDisconnect?.cancel()
        myTableConnectionRef?.setValue(null)
        myTableConnectionRef?.onDisconnect()
        myTableConnectionRef = null
        _tableStateFlow.update { null }
        collectTableJob?.cancel()
        collectTableJob = null
        tableConnectionJob?.cancel()
        tableConnectionJob = null
        currentTableId = null
    }

    override suspend fun sendTable(
        table: Table
    ) {
        val tableMap = tableMapper.toMap(table)
        val tableRef = tablesRef.child(table.id.value)
        tableRef.setValue(tableMap)
    }

    override fun startCurrentTableConnectionIfNeed(tableId: TableId) {
        if (tableConnectionJob != null) {
            // すでに接続中の場合は無視
            return
        }
        if (currentTableId != null && currentTableId == tableId) {
            tableConnectionJob = appCoroutineScope.launch {
                val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
                myTableConnectionRef =
                    tableConnectionRef.child(tableId.value).child(myPlayerId.value)
                myTableConnectionRef?.setValue(true)
                myTableConnectionRefOnDisconnect = myTableConnectionRef?.onDisconnect()
                myTableConnectionRefOnDisconnect?.removeValue { error, _ ->
                    if (error != null) {
                        Log.e(
                            "TableRepository",
                            "Failed to set onDisconnect removeValue: ${error.message} $myPlayerId"
                        )
                    }
                    tableConnectionJob = null
                }
            }
        }
    }

    override fun stopCurrentTableCurrentTableConnection(tableId: TableId) {
        if (currentTableId != null && currentTableId == tableId) {
            myTableConnectionRefOnDisconnect?.cancel()
            myTableConnectionRef?.setValue(null)
        }
    }

    private fun firebaseDatabaseTableFlow(tableId: TableId): Flow<DataSnapshot> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TableRepository", "Failed to read table data: ${error.message}")
            }
        }
        tablesRef.child(tableId.value).addValueEventListener(listener)

        awaitClose {
            tablesRef.child(tableId.value).removeEventListener(listener)
        }
    }

    private fun firebaseDatabaseTableConnectionFlow(tableId: TableId): Flow<DataSnapshot> =
        callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(
                        "TableRepository",
                        "Failed to read table connection data: ${error.message}"
                    )
                }
            }
            tableConnectionRef.child(tableId.value).addValueEventListener(listener)

            awaitClose {
                tableConnectionRef.child(tableId.value).removeEventListener(listener)
            }
        }

    override suspend fun isExistsTable(tableId: TableId): Boolean {
        return withContext(ioDispatcher) {
            tablesRef.child(tableId.value)
                .get()
                .await()
                .exists()
        }
    }

    override suspend fun renameTableBasePlayer(
        tableId: TableId,
        indexOfBasePlayers: Long,
        playerId: PlayerId,
        name: String
    ) {
        val tableRef = tablesRef.child(tableId.value)
        val nameRef = tableRef.child("basePlayers/${indexOfBasePlayers}/name")
        nameRef.setValue(name)
    }

    override suspend fun renameTableWaitPlayer(
        tableId: TableId,
        indexOfWaitPlayers: Long,
        playerId: PlayerId,
        name: String
    ) {
        val tableRef = tablesRef.child(tableId.value)
        val nameRef = tableRef.child("waitPlayers/${indexOfWaitPlayers}/name")
        nameRef.setValue(name)
    }
}