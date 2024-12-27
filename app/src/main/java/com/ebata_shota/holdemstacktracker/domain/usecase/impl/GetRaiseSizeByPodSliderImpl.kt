package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByPodSlider
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.roundToInt

class GetRaiseSizeByPodSliderImpl
@Inject
constructor(
    private val prefRepository: PrefRepository,
) : GetRaiseSizeByPodSlider {

    /**
     * Podスライダーから
     * Raiseサイズを取得する
     */
    override suspend fun invoke(
        betViewMode: BetViewMode,
        totalPodSize: Double,
        stackSize: Double,
        pendingBetSize: Double,
        minRaiseSize: Double,
        sliderPosition: Float,
    ): Double {
        val podSliderRatioMax = prefRepository.podSliderMaxRatio.first()
        // スライダー最大位置でのRaiseサイズ
        val raiseSizeOfSliderMaxPosition = totalPodSize * podSliderRatioMax
        // スライダーポジションをそのままRaiseサイズに変換
        val raiseSizeBySliderPosition = when (betViewMode) {
            BetViewMode.Number -> {
                (raiseSizeOfSliderMaxPosition * sliderPosition).roundToInt().toDouble()
            }

            BetViewMode.BB -> {
                (raiseSizeOfSliderMaxPosition * 10 * sliderPosition).roundToInt() / 10.0
            }
        }
        // 引き上げサイズ
        val raiseUpSize = raiseSizeBySliderPosition - pendingBetSize
        // 最低の引き上げサイズ
        val minRiseUpSize = minRaiseSize - pendingBetSize
        // stackと最低引き上げ額の範囲でRaiseサイズを収める
        val raiseSize = when {
            raiseUpSize > stackSize -> {
                // 上回っている場合は、スタックを引き上げサイズに
                stackSize
            }

            raiseUpSize >= minRiseUpSize -> {
                // スタック > 引き上げサイズ >= 最低引き上げサイズ
                raiseSizeBySliderPosition
            }

            else -> {
                // 最低引き上げサイズを下回っている場合は、現在の最低額
                minRaiseSize
            }
        }
        return raiseSize
    }
}