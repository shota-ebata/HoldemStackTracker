package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.TableStateMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TableStateRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase,
    private val prefRepository: PrefRepository,
    private val tableMapper: TableStateMapper,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
) : TableStateRepository {

    private val tablesRef: DatabaseReference = firebaseDatabase.getReference(
        "tables"
    )

    private val _tableStateFlow = MutableSharedFlow<TableState>()
    override val tableStateFlow: Flow<TableState> = _tableStateFlow.asSharedFlow()

    override fun startCollectTableStateFlow(tableId: TableId) {
        appCoroutineScope.launch {
            val flow = callbackFlow<TableState> {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val tableMap: Map<*, *> = snapshot.value as Map<*, *>
                            trySend(tableMapper.mapToTableState(tableId, tableMap))
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