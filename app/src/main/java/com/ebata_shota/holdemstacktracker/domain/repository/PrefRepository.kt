package com.ebata_shota.holdemstacktracker.domain.repository

import kotlinx.coroutines.flow.Flow


interface PrefRepository {
    val myPlayerId: Flow<String>
    val myName: Flow<String>

    suspend fun setMyName(myName: String)
}