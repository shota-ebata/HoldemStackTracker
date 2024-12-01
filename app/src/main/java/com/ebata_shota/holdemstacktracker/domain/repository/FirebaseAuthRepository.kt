package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    val myPlayerIdFlow: Flow<PlayerId>
    fun signInAnonymously()
}