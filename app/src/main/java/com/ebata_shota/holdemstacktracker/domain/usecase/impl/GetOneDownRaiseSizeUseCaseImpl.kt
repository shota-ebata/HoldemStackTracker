package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getDigitCount
import com.ebata_shota.holdemstacktracker.domain.util.getMinNumberForDigits
import com.ebata_shota.holdemstacktracker.domain.util.roundDownToDigit
import javax.inject.Inject

class GetOneDownRaiseSizeUseCaseImpl
@Inject
constructor() : GetOneDownRaiseSizeUseCase {

    override suspend fun invoke(
        currentRaiseSize: Int,
        minRaiseSize: Int,
    ): Int {
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
            if (currentDigitCount > getDigitCount(nextRaiseSize)) {
                // 桁数下がりそうなら、下げ幅を変更する
                currentRaiseSize - getMinNumberForDigits(changeDigit - 1)
            } else {
                // 桁数が変わらないなら、そのままの下げ幅を利用する
                nextRaiseSize
            }
        }

        return if (nextRaiseSize < minRaiseSize) {
            minRaiseSize
        } else {
            nextRaiseSize
        }
    }
}