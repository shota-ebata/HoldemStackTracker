package com.ebata_shota.holdemstacktracker.infra.repository

import android.util.Log
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundTableException
import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerBase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.infra.extension.runTransaction
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.BAN_PLAYER_IDS
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.BASE_PLAYER_IDS
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.CURRENT_GAME_ID
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.PLAYER_CONNECTION_INFO
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.PLAYER_NAME_INFO
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.PLAYER_ORDER
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.PLAYER_SEATED_INFO
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.PLAYER_STACK_INFO
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE_BB_SIZE
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE_DEFAULT_STACK
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE_SB_SIZE
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE_TYPE
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.RULE_TYPE_RING_GAME
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.TABLE_STATUS
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.TABLE_VERSION
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.UPDATE_TIME
import com.ebata_shota.holdemstacktracker.infra.mapper.TableMapper.Companion.WAIT_PLAYER_IDS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.OnDisconnect
import com.google.firebase.database.ServerValue
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private var myTableConnectionRef: DatabaseReference? = null
    private var myTableConnectionRefOnDisconnect: OnDisconnect? = null

    private val _tableStateFlow = MutableStateFlow<Result<Table>?>(null)
    override val tableStateFlow: StateFlow<Result<Table>?> = _tableStateFlow.asStateFlow()

    override suspend fun createNewTable(
        tableId: TableId,
        rule: Rule,
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
                        isSeated = true,
                        isConnected = false, // falseにする。true化は別の場所でやるので。
                        lostConnectTimestamp = null,
                    )
                ),
                waitPlayerIds = emptyMap(),
                banPlayerIds = emptyList(),
                tableStatus = TableStatus.PREPARING,
                currentGameId = null,
                startTime = null,
                tableCreateTime = tableCreateTime,
                updateTime = tableCreateTime
            )
            val tableMap = tableMapper.toMap(table)
            val tableRef = tablesRef.child(table.id.value)
            val basePlayerIdsKey = tableRef
                .child(BASE_PLAYER_IDS)
                .push().key!!
            tableRef.runTransaction { currentData ->
                currentData.setValue(tableMap)
                currentData
                    .child(BASE_PLAYER_IDS)
                    .child(basePlayerIdsKey)
                    .setValue(myPlayerId.value)
            }
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
        collectTableJob = appCoroutineScope.launch(ioDispatcher) {
            startCurrentTableConnectionIfNeed(tableId) // TODO: いらんかも、いらんなら消す

            launch {
                firebaseDatabaseTableFlow(tableId).collect { tableSnapshot: DataSnapshot ->
                    if (tableSnapshot.exists()) {
                        val tableState = tableMapper.mapToTableState(
                            tableId = tableId,
                            tableSnapshot = tableSnapshot,
                        )
                        _tableStateFlow.emit(Result.success(tableState))
                    } else {
                        // FIXME: 例外の種類を豊富にしたい
                        _tableStateFlow.emit(Result.failure(NotFoundTableException()))
                    }
                }
            }
        }
    }

    override fun stopCollectTableFlow() {
        myTableConnectionRefOnDisconnect?.cancel()
        myTableConnectionRefOnDisconnect = null
        myTableConnectionRef?.setValue(ServerValue.TIMESTAMP)
        myTableConnectionRef = null
        // TODO: 旧テーブルを離席状態にするなど
        _tableStateFlow.update { null }
        collectTableJob?.cancel()
        collectTableJob = null
        tableConnectionJob?.cancel()
        tableConnectionJob = null
        currentTableId = null
    }

    override suspend fun sendTable(
        table: Table,
    ) {
        val tableMap = tableMapper.toMap(table)
        val tableRef = tablesRef.child(table.id.value)
        tableRef.setValue(tableMap)
    }

    override suspend fun addBasePlayer(
        tableId: TableId,
        playerId: PlayerId,
        name: String,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)

        val waitPlayerIdsKey = tableRef
            .child(WAIT_PLAYER_IDS)
            .push().key!!

        val basePlayerIdsKey = tableRef
            .child(BASE_PLAYER_IDS)
            .push().key!!

        tableRef.runTransaction { currentData ->
            currentData
                .child(PLAYER_NAME_INFO)
                .child(playerId.value)
                .setValue(name)
            currentData
                .child(PLAYER_SEATED_INFO)
                .child(playerId.value)
                .setValue(true)
            currentData
                .child(PLAYER_STACK_INFO)
                .child(playerId.value)
                .setValue(table.rule.defaultStack)
            currentData
                .child(WAIT_PLAYER_IDS)
                .child(waitPlayerIdsKey)
                .setValue(playerId.value)
            currentData
                .child(BASE_PLAYER_IDS)
                .child(basePlayerIdsKey)
                .setValue(playerId.value)
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun addPlayerOrder(
        tableId: TableId,
        newPlayerOrder: List<PlayerId>,
        addPlayerIds: Map<String, PlayerId>,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }

        val tableRef = tablesRef.child(tableId.value)

        tableRef.runTransaction { currentData ->
            addPlayerIds.forEach {
                currentData
                    .child(WAIT_PLAYER_IDS)
                    .child(it.key)
                    .setValue(null)
            }

            currentData
                .child(PLAYER_ORDER)
                .setValue(newPlayerOrder.map { it.value })

            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun updateBasePlayer(
        tableId: TableId,
        playerId: PlayerId,
        newStack: Int?,
        newIsSeated: Boolean?,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        if (newStack == null && newIsSeated == null) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            if (newStack != null) {
                currentData
                    .child(PLAYER_STACK_INFO)
                    .child(playerId.value)
                    .setValue(newStack)
            }
            if (newIsSeated != null) {
                currentData
                    .child(PLAYER_SEATED_INFO)
                    .child(playerId.value)
                    .setValue(newIsSeated)
            }
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun updateBasePlayerStacks(
        tableId: TableId,
        stacks: Map<PlayerId, Int>,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            val playerStackInfoRef = currentData.child(PLAYER_STACK_INFO)
            stacks.forEach { (key, value) ->
                playerStackInfoRef
                    .child(key.value)
                    .setValue(value)
            }
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun updatePlayerOrder(
        tableId: TableId,
        playerOrder: List<PlayerId>,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            currentData
                .child(PLAYER_ORDER)
                .setValue(playerOrder.map { it.value })
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun updateRule(
        tableId: TableId,
        rule: Rule,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            currentData.child(RULE).value = when (rule) {
                is Rule.RingGame -> {
                    mapOf(
                        RULE_TYPE to RULE_TYPE_RING_GAME,
                        RULE_SB_SIZE to rule.sbSize,
                        RULE_BB_SIZE to rule.bbSize,
                        RULE_DEFAULT_STACK to rule.defaultStack
                    )
                }
            }
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun updateTableStatus(
        tableId: TableId,
        tableStatus: TableStatus,
        gameId: GameId?,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            currentData
                .child(TABLE_STATUS)
                .value = tableStatus.name
            if (gameId != null) {
                currentData
                    .child(CURRENT_GAME_ID)
                    .value = gameId.value
            }
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    override suspend fun addBanPlayers(
        tableId: TableId,
        newPlayerOrder: List<PlayerId>,
        banPlayerIds: List<PlayerId>,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            val banPlayerIdsData = currentData
                .child(BAN_PLAYER_IDS)

            var isUpdated = false
            banPlayerIds.forEach { playerId ->
                val isNotBanPlayer = banPlayerIdsData.children.map { it.value }.none { it == playerId }
                if (isNotBanPlayer) {
                    // BANされてないなら、BANリストに追加
                    val banPlayerIdsKey = tableRef
                        .child(BAN_PLAYER_IDS)
                        .push().key!!

                    currentData
                        .child(PLAYER_ORDER)
                        .value = newPlayerOrder.map { it.value }

                    currentData
                        .child(BAN_PLAYER_IDS)
                        .child(banPlayerIdsKey)
                        .value = playerId.value
                    isUpdated = true
                }
            }
            if (isUpdated) {
                currentData.updateVersionAndUpdateTimeInTransaction()
            }
        }
    }

    override suspend fun updateSeat(
        tableId: TableId,
        playerId: PlayerId,
        isSeat: Boolean,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            currentData
                .child(PLAYER_SEATED_INFO)
                .child(playerId.value)
                .setValue(isSeat)
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    private val connectionMutex = Mutex()
    override fun startCurrentTableConnectionIfNeed(tableId: TableId) {
        if (currentTableId != null && currentTableId == tableId) {
            appCoroutineScope.launch(ioDispatcher) {
                connectionMutex.withLock {
                    val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
                    val isConnected = (tablesRef
                        .child(tableId.value)
                        .child(PLAYER_CONNECTION_INFO)
                        .child(myPlayerId.value)
                        .get()
                        .await()
                        .value as? Boolean) ?: false
                    if (isConnected) {
                        // すでに接続中の場合は無視
                        return@launch
                    }
                    val tableRef = tablesRef.child(tableId.value)
                    tableRef.runTransaction { currentData ->
                        currentData
                            .child(PLAYER_CONNECTION_INFO)
                            .child(myPlayerId.value)
                            .value = true
                        currentData.updateVersionAndUpdateTimeInTransaction()
                    }
                    myTableConnectionRef = createTableConnectionRef(
                        tableId = tableId,
                        myPlayerId = myPlayerId,
                    )
                    myTableConnectionRefOnDisconnect = myTableConnectionRef?.onDisconnect()?.also {
                        it.setValue(ServerValue.TIMESTAMP)
                    }
                }
            }
        }
    }

    private fun createTableConnectionRef(
        tableId: TableId,
        myPlayerId: PlayerId,
    ): DatabaseReference = tablesRef
        .child(tableId.value)
        .child(PLAYER_CONNECTION_INFO)
        .child(myPlayerId.value)

    override fun stopCurrentTableCurrentTableConnection(tableId: TableId) {
        if (currentTableId != null && currentTableId == tableId) {
            myTableConnectionRefOnDisconnect?.cancel()
            myTableConnectionRefOnDisconnect = null
            myTableConnectionRef?.setValue(ServerValue.TIMESTAMP)
            myTableConnectionRef = null
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
        name: String,
    ) {
        val table = tableStateFlow.value?.getOrNull() ?: return
        if (tableId != table.id) {
            return
        }
        val tableRef = tablesRef.child(tableId.value)
        tableRef.runTransaction { currentData ->
            currentData
                .child(PLAYER_NAME_INFO)
                .child(playerId.value)
                .value = name
            currentData.updateVersionAndUpdateTimeInTransaction()
        }
    }

    /**
     * TABLE_VERSIONとUPDATE_TIMEを更新する
     * TableRefに対してのみ実行することを想定している
     */
    private fun MutableData.updateVersionAndUpdateTimeInTransaction() {
        val currentVersion = child(TABLE_VERSION).value as Long
        child(TABLE_VERSION).value = currentVersion + 1
        // ServerValue.TIMESTAMP だと無駄に2回発火するっぽい？
        // ので、 Instant.now().toEpochMilli()を送っている
        child(UPDATE_TIME).value = Instant.now().toEpochMilli()
    }
}