package com.ebata_shota.holdemstacktracker.domain.repository

import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    val uidFlow: Flow<String>
    fun signInAnonymously()
}