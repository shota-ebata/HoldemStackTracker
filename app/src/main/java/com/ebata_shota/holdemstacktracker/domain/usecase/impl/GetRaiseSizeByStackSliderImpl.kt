package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSlider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class GetRaiseSizeByStackSliderImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetRaiseSizeByStackSlider {

    /**
     * Stackスライダーから
     * Raiseサイズを取得する
     */
    override suspend fun invoke(
        betViewMode: BetViewMode,
        stackSize: Double,
        minRaiseSize: Double,
        myPendingBetSize: Double,
        sliderPosition: Float,
    ): Double = withContext(dispatcher) {
        // 追加でBetするサイズ
        val raiseUpSize = when (betViewMode) {
            BetViewMode.Number -> {
                (stackSize * sliderPosition).roundToInt().toDouble()
            }

            BetViewMode.BB -> {
                (stackSize * 10 * sliderPosition).roundToInt() / 10.0
            }
        }
        // 最低の引き上げ幅
        val minRiseUpSize = minRaiseSize - myPendingBetSize
        val raiseSize: Double = if (raiseUpSize >= minRiseUpSize) {
            // 最低Betサイズを超えている場合は
            // 追加Betサイズ + 今場に出ているベットサイズ
            raiseUpSize + myPendingBetSize
        } else {
            // 下回っている場合は、現在の最低額
            minRaiseSize
        }
        return@withContext raiseSize
    }
}