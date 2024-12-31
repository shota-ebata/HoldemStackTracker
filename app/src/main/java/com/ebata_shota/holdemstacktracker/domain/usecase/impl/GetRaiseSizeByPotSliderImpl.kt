package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByPotSlider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class GetRaiseSizeByPotSliderImpl
@Inject
constructor(
    private val prefRepository: PrefRepository,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetRaiseSizeByPotSlider {

    /**
     * Potスライダーから
     * Raiseサイズを取得する
     */
    override suspend fun invoke(
        totalPotSize: Int,
        stackSize: Int,
        pendingBetSize: Int,
        minRaiseSize: Int,
        sliderPosition: Float,
    ): Int = withContext(dispatcher) {
        val potSliderRatioMax = prefRepository.potSliderMaxRatio.first()
        // スライダー最大位置でのRaiseサイズ
        val raiseSizeOfSliderMaxPosition = totalPotSize * potSliderRatioMax
        // スライダーポジションをそのままRaiseサイズに変換
        val raiseSizeBySliderPosition = (raiseSizeOfSliderMaxPosition * sliderPosition).roundToInt()
        // 引き上げサイズ
        val raiseUpSize = raiseSizeBySliderPosition - pendingBetSize
        // 最低の引き上げサイズ
        val minRiseUpSize = minRaiseSize - pendingBetSize
        // stackと最低引き上げ額の範囲でRaiseサイズを収める
        val raiseSize = when {
            raiseSizeBySliderPosition > stackSize + pendingBetSize -> {
                // 上回っている場合は、スタックを引き上げサイズに
                stackSize + pendingBetSize
            }

            raiseSizeBySliderPosition >= minRaiseSize -> {
                // スタック > 引き上げサイズ >= 最低引き上げサイズ
                raiseSizeBySliderPosition
            }

            else -> {
                // 最低引き上げサイズを下回っている場合は、現在の最低額
                minRaiseSize
            }
        }
        return@withContext raiseSize
    }
}