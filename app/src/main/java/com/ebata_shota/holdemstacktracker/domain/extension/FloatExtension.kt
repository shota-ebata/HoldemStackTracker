package com.ebata_shota.holdemstacktracker.domain.extension

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * 四捨五入する
 * 少数桁を指定して丸める
 * @param decimalPlace 小数点第[decimalPlace]位 （1以上の整数）
 */
fun Float.roundDigit(decimalPlace: Int): Float {
    if (decimalPlace < 1) {
        // 小数点第一位を下回るならそのまま帰す
        throw IllegalArgumentException("decimalPlaceは1以上の整数")
    }
    val pow = 10.0f.pow(decimalPlace - 1)
    return ((this * pow).roundToInt() / pow)
}