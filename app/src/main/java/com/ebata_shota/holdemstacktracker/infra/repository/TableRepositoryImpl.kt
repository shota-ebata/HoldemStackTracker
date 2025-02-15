package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundTableException
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class TableRepositoryImpl
@Inject
constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val prefRepository: PrefRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val tableSummaryRepository: TableSummaryRepository,
    private val tableMapper: TableMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @CoroutineDispatcherIO
    private val ioDispatcher: CoroutineDispatcher
) : TableRepository {

    private val tablesRef: DatabaseReference = firebaseDatabase.getReference(
        if (BuildConfig.DEBUG) {
            "debug_tables"
        } else {
            "tables"
        }
    )

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
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                hostPlayerId = myPlayerId,
                rule = rule,
                playerOrder = listOf(myPlayerId),
                btnPlayerId = myPlayerId,
                basePlayers = listOf(
                    PlayerBaseState(
                        id = myPlayerId,
                        name = myName,
                        stack = rule.defaultStack,
                        isLeaved = false,
                    )
                ),
                waitPlayerIds = emptyList(),
                tableStatus = TableStatus.PREPARING,
                startTime = null,
                tableCreateTime = tableCreateTime,
                updateTime = tableCreateTime
            )
            sendTable(table)
        }
    }

    private var collectTableJob: Job? = null
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
            firebaseDatabaseTableFlow(tableId)
                .collect { tableResult ->
                    val table = tableResult.getOrNull()
                    val exception = tableResult.exceptionOrNull()
                    when {
                        table != null -> {
                            tableSummaryRepository.saveTable(table)
                            // FIXME: 無駄にResultに再ラップしているので修正したい
                            _tableStateFlow.emit(Result.success(table))
                        }

                        exception != null -> {
                            // FIXME: 無駄にResultに再ラップしているので修正したい
                            _tableStateFlow.emit(Result.failure(exception))
                        }
                    }
                }
        }
    }

    override fun stopCollectTableFlow() {
        _tableStateFlow.update { null }
        collectTableJob?.cancel()
        collectTableJob = null
        currentTableId = null
    }

    override suspend fun sendTable(
        table: Table
    ) {
        val tableMap = tableMapper.toMap(table)
        val tableRef = tablesRef.child(table.id.value)
        tableRef.setValue(tableMap)
    }

    private fun firebaseDatabaseTableFlow(tableId: TableId): Flow<Result<Table>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tableMap: Map<*, *> = snapshot.value as Map<*, *>
                    val tableState = tableMapper.mapToTableState(tableId, tableMap)
                    trySend(Result.success(tableState))
                } else {
                    trySend(Result.failure(NotFoundTableException()))
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

    override suspend fun renameTableBasePlayer(
        tableId: TableId,
        indexOfBasePlayers: Long,
        playerId: PlayerId,
        name: String
    ) {
        // FIXME: pathは定数を使いたい
        val nameRef = firebaseDatabase.getReference(
            "tables/${tableId.value}/basePlayers/${indexOfBasePlayers}/name"
        )
        nameRef.setValue(name)
    }

    override suspend fun renameTableWaitPlayer(
        tableId: TableId,
        indexOfWaitPlayers: Long,
        playerId: PlayerId,
        name: String
    ) {
        // FIXME: pathは定数を使いたい
        val nameRef = firebaseDatabase.getReference(
            "tables/${tableId.value}/waitPlayers/${indexOfWaitPlayers}/name"
        )
        nameRef.setValue(name)
    }
}