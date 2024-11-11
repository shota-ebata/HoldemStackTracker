package com.ebata_shota.holdemstacktracker.domain.repository

import kotlinx.coroutines.flow.Flow


interface PrefRepository {
    val myName: Flow<String>
    suspend fun saveMyName(myName: String)
}