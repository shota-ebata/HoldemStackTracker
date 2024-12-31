package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getDigitCount
import com.ebata_shota.holdemstacktracker.domain.util.getMinNumberForDigits
import com.ebata_shota.holdemstacktracker.domain.util.roundDownToDigit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetOneDownRaiseSizeUseCaseImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : GetOneDownRaiseSizeUseCase {

    override suspend fun invoke(
        currentRaiseSize: Int,
        minRaiseSize: Int,
    ): Int = withContext(dispatcher) {
        val currentDigitCount = getDigitCount(currentRaiseSize)
        val changeDigit = if (currentDigitCount - 1 > 0) {
            currentDigitCount - 1
        } else {
            currentDigitCount
        }

        val roundDownRaiseSize = roundDownToDigit(currentRaiseSize, changeDigit - 1)
        val nextRaiseSize = if (currentRaiseSize != roundDownRaiseSize) {
            // 現在のRaiseサイズと切り下げたサイズが一致しないなら
            // 端数を下げただけなのでそれを適用する
            roundDownRaiseSize
        } else {
            // 端数がないので、切りよく下げる
            val nextRaiseSize = currentRaiseSize - getMinNumberForDigits(changeDigit)
            // 現在のRaiseSizeから下げる
            if (currentDigitCount > getDigitCount(nextRaiseSize) && changeDigit - 1 > 0) {
                // 桁数下がりそうなら、下げ幅を変更する
                currentRaiseSize - getMinNumberForDigits(changeDigit - 1)
            } else {
                // 桁数が変わらないなら、そのままの下げ幅を利用する
                nextRaiseSize
            }
        }

        return@withContext if (nextRaiseSize < minRaiseSize) {
            minRaiseSize
        } else {
            nextRaiseSize
        }
    }
}