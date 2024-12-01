package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import kotlinx.coroutines.flow.Flow


interface PrefRepository {
    val myName: Flow<String?>
    suspend fun saveMyName(myName: String)

    /**
     * Number or BB 表示
     */
    val defaultBetViewMode: Flow<BetViewMode>
    suspend fun saveDefaultBetViewMode(betViewMode: BetViewMode)

    /**
     * デフォルトSBサイズ（Numberモード）
     */
    val defaultSizeOfSbOfNumberMode: Flow<Int>
    suspend fun saveDefaultSizeOfSbOfNumberMode(value: Int)

    /**
     * デフォルトSBサイズ（BBモード）
     */
    val defaultSizeOfSbOfBbMode: Flow<Double>
    suspend fun saveDefaultSizeOfSbOfBbMode(value: Double)

    /**
     * デフォルトBBサイズ（Numberモードしかない）
     */
    val defaultSizeOfBbOfNumberMode: Flow<Int>
    suspend fun saveDefaultSizeOfBbOfNumberMode(value: Int)

    /**
     * デフォルトスタックサイズ（Numberモード）
     */
    val defaultStackSizeOfNumberMode: Flow<Int>
    suspend fun saveDefaultStackSizeOfNumberMode(value: Int)

    /**
     * デフォルトスタックサイズ（BBモード）
     */
    val defaultStackSizeOfBbMode: Flow<Double>
    suspend fun saveDefaultStackSizeOfBBMode(value: Double)
}