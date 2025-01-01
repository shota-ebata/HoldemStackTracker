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
     * デフォルトSBサイズ
     */
    val defaultSizeOfSb: Flow<Int>
    suspend fun saveDefaultSizeOfSb(value: Int)

    /**
     * デフォルトBBサイズ
     */
    val defaultSizeOfBb: Flow<Int>
    suspend fun saveDefaultSizeOfBb(value: Int)

    /**
     * デフォルトスタックサイズ
     */
    val defaultStackSize: Flow<Int>
    suspend fun saveDefaultStackSize(value: Int)

    /**
     * RaiseUpサイズのスライダーステップON_OFF
     */
    val isEnableRaiseUpSliderStep: Flow<Boolean>
    suspend fun saveEnableRaiseUpSliderStep(value: Boolean)

    /**
     * Potスライダーの最大位置の比率
     * 例）2 ： 最大位置が 2 Pot
     */
    val potSliderMaxRatio: Flow<Int>
    suspend fun savePotSliderMaxRatio(value: Int)
}