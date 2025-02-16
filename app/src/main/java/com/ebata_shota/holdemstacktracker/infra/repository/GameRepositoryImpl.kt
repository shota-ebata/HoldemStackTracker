package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundGameException
import com.ebata_shota.holdemstacktracker.domain.model.ActionHistory
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseHistory
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.infra.mapper.GameMapper
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
import javax.inject.Inject

class GameRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val gameMapper: GameMapper,
    private val phaseHistoryRepository: PhaseHistoryRepository,
    private val actionHistoryRepository: ActionHistoryRepository,
    private val tableRepository: TableRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getCurrentPlayerId: GetCurrentPlayerIdUseCase,
    private val getNextAutoAction: GetNextAutoActionUseCase,
    private val addBetPhaseActionInToGame: AddBetPhaseActionInToGameUseCase,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
    @CoroutineDispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : GameRepository {

    @Suppress("KotlinConstantConditions")
    private val gamesRef: DatabaseReference = firebaseDatabase.getReference(
        if (BuildConfig.BUILD_TYPE == "debug") {
            "debug_games"
        } else {
            "games"
        }
    )

    private val _gameStateFlow = MutableStateFlow<Result<Game>?>(null)
    override val gameStateFlow: StateFlow<Result<Game>?> = _gameStateFlow.asStateFlow()

    private var collectGameJob: Job? = null
    var currentTableId: TableId? = null

    override fun startCollectGameFlow(tableId: TableId) {
        if (currentTableId == tableId) {
            // 同じテーブルだった場合はすでに監視中なので無視
            return
        }
        stopCollectGameFlow()
        currentTableId = tableId
        collectGameJob = appCoroutineScope.launch {
            firebaseDatabaseGameFlow(tableId).collect { gameResult ->
                // 必要なら、最新ActionをDBに保存
                saveActionIfNeed(
                    tableId = tableId,
                    gameResult = gameResult,
                )
                // FIXME: Tableの購読開始したほうがいいかも？
                val table = tableRepository.tableStateFlow.value?.getOrNull()
                val game = gameResult.getOrNull()
                if (table != null && game != null) {
                    val autoAction: BetPhaseAction? = getAutoActionIfNeed(game, table)
                    if (autoAction != null) {
                        // オートアクションがあるなら、それを使って新しいGameを生成
                        val updatedGame = addBetPhaseActionInToGame.invoke(
                            btnPlayerId = table.btnPlayerId,
                            currentGame = game,
                            betPhaseAction = autoAction,
                        )
                        // 更新実行
                        sendGame(
                            tableId = tableId,
                            newGame = updatedGame
                        )
                    } else {
                        // オートアクションがない場合だけ、StateFlowに反映
                        _gameStateFlow.update { gameResult }
                    }
                }
            }
        }
    }

    private suspend fun getAutoActionIfNeed(
        game: Game,
        table: Table,
    ): BetPhaseAction? {
        val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
        val currentBetPhase = try {
            getLastPhaseAsBetPhase.invoke(game.phaseList)
        } catch (e: IllegalStateException) {
            null
        }
        val currentPlayerId = currentBetPhase?.let {
            getCurrentPlayerId.invoke(
                btnPlayerId = table.btnPlayerId,
                playerOrder = game.playerOrder,
                currentBetPhase = currentBetPhase
            )
        }
        // 自分の番でAutoActionが発生するか確認する
        val isCurrentPlayer: Boolean = myPlayerId == currentPlayerId
        val autoAction: BetPhaseAction? = if (isCurrentPlayer) {
            // AutoActionする
            getNextAutoAction.invoke(
                playerId = myPlayerId,
                rule = table.rule,
                game = game,
            )
        } else {
            null
        }
        return autoAction
    }

    private suspend fun saveActionIfNeed(
        tableId: TableId,
        gameResult: Result<Game>,
    ) = withContext(dispatcher) {
        gameResult.getOrNull()?.let { game ->
            game.phaseList.forEach { phase ->
                val phaseHistory = phaseHistoryRepository.getPhaseHistory(
                    tableId = tableId,
                    phaseId = phase.phaseId
                )
                if (phaseHistory == null) {
                    phaseHistoryRepository.savePhaseHistory(
                        phaseHistory = PhaseHistory(
                            tableId = tableId,
                            phaseId = phase.phaseId,
                            isFinished = false,
                            timestamp = game.updateTime
                        )
                    )
                }
            }
            game.phaseList.lastOrNull()?.let { phase ->
                if (phase is Phase.BetPhase) {
                    phase.actionStateList.lastOrNull()?.let { action ->
                        val actionId = action.actionId
                        val actionHistory = actionHistoryRepository.getActionHistory(
                            tableId = tableId,
                            actionId = actionId
                        )
                        if (actionHistory == null) {
                            // Action履歴に保存されていない場合は、新規保存する
                            actionHistoryRepository.saveActionHistory(
                                actionHistory = ActionHistory(
                                    tableId = tableId,
                                    actionId = actionId,
                                    hadSeen = false,
                                    timestamp = game.updateTime
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun firebaseDatabaseGameFlow(tableId: TableId): Flow<Result<Game>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameMap: Map<*, *> = snapshot.value as Map<*, *>
                    val game = gameMapper.mapToGame(gameMap)
                    trySend(Result.success(game))
                } else {
                    trySend(Result.failure(NotFoundGameException()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read game data: ${error.message}")
            }
        }
        gamesRef.child(tableId.value).addValueEventListener(listener)

        awaitClose {
            gamesRef.child(tableId.value).removeEventListener(listener)
        }
    }

    override fun stopCollectGameFlow() {
        _gameStateFlow.update { null }
        collectGameJob?.cancel()
        collectGameJob = null
        currentTableId = null
    }

    /**
     * Gameを更新してFirebaseRealtimeDatabaseに送る
     *
     * @param newGame 新しいGame
     */
    override suspend fun sendGame(
        tableId: TableId,
        newGame: Game,
    ) {
        val gameHashMap = gameMapper.mapToHashMap(
            newGame = newGame.copy(version = newGame.version + 1L)
        )
        val gameRef = gamesRef.child(tableId.value)
        gameRef.setValue(gameHashMap)
    }
}