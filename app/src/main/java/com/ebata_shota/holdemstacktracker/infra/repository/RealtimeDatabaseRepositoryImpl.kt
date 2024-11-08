package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.RealtimeDatabaseRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class RealtimeDatabaseRepositoryImpl
@Inject
constructor(
    firebaseDatabase: FirebaseDatabase
) : RealtimeDatabaseRepository {

    private val gamesRef: DatabaseReference = firebaseDatabase.getReference(
        "games"
    )

    override suspend fun setGameHashMap(
        tableId: TableId,
        gameHashMap: HashMap<String, Any>
    ) {
        val gameRef = gamesRef.child(tableId.value)
        gameRef.setValue(gameHashMap)
    }
}