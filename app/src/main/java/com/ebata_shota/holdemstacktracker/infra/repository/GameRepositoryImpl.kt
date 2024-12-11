package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.exception.NotFoundGameException
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.GameMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val gameMapper: GameMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
) : GameRepository {

    private val gamesRef: DatabaseReference = firebaseDatabase.getReference(
        "games"
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
            firebaseDatabaseGameFlow(tableId)
                .collect { gameResult ->
                    _gameStateFlow.update { gameResult }
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
        newGame: Game
    ) {
        val gameHashMap = gameMapper.mapToHashMap(newGame)
        val gameRef = gamesRef.child(tableId.value)
        gameRef.setValue(gameHashMap)
    }
}