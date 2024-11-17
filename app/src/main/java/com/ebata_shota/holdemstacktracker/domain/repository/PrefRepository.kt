package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import kotlinx.coroutines.flow.Flow


interface PrefRepository {
    val myName: Flow<String>
    suspend fun saveMyName(myName: String)

    /**
     * Number or BB 表示
     */
    val defaultBetViewMode: Flow<BetViewMode>
    suspend fun saveDefaultBetViewMode(betViewMode: BetViewMode)

    /**
     * デフォルトSBサイズ
     */
    val defaultSizeOfSB: Flow<Double>
    suspend fun saveDefaultSizeOfSB(value: Double)

    /**
     * デフォルトBBサイズ
     */
    val defaultSizeOfBB: Flow<Double>
    suspend fun saveDefaultSizeOfBB(value: Double)

    /**
     * デフォルトスタックサイズ（Numberモード）
     */
    val defaultStackSizeOfNumberMode: Flow<Double>
    suspend fun saveDefaultStackSizeOfNumberMode(value: Double)

    /**
     * デフォルトスタックサイズ（BBモード）
     */
    val defaultStackSizeOfBBMode: Flow<Double>
    suspend fun saveDefaultStackSizeOfBBMode(value: Double)
}