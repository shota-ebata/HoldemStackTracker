package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameStateRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val gameMapper: GameMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
) : GameStateRepository {

    private val gamesRef: DatabaseReference = firebaseDatabase.getReference(
        "games"
    )

    private val _gameFlow = MutableSharedFlow<GameState>()
    override val gameFlow: Flow<GameState> = _gameFlow.asSharedFlow()

    private var collectGameJob: Job? = null

    override fun startCollectGameFlow(tableId: TableId) {
        collectGameJob = appCoroutineScope.launch {
            val flow = callbackFlow {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val gameMap: Map<*, *> = snapshot.value as Map<*, *>
                            val gameState = gameMapper.mapToGameState(gameMap)
                            trySend(gameState)
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
            flow.collect(_gameFlow)
        }
    }

    override fun stopCollectGameFlow() {
        collectGameJob?.cancel()
    }

    /**
     * GameStateを更新してFirebaseRealtimeDatabaseに送る
     *
     * @param newGameState 新しいGameState
     */
    override suspend fun sendGameState(
        tableId: TableId,
        newGameState: GameState
    ) {
        val gameHashMap = gameMapper.mapToHashMap(newGameState)
        val gameRef = gamesRef.child(tableId.value)
        gameRef.setValue(gameHashMap)
    }
}