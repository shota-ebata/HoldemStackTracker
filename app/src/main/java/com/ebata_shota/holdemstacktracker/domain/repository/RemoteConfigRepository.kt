package com.ebata_shota.holdemstacktracker.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface RemoteConfigRepository {
    val isMaintenance: StateFlow<Boolean>
    val minVersionCode: StateFlow<Int>
}