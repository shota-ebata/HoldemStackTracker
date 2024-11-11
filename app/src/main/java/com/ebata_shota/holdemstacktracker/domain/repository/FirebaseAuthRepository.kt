package com.ebata_shota.holdemstacktracker.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface FirebaseAuthRepository {
    val uidFlow: SharedFlow<String>
    fun signInAnonymously()
}