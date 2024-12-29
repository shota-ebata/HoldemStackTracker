package com.ebata_shota.holdemstacktracker.domain.util

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

/**
 * 桁数取得
 * 234 → 3
 * 1234 → 4
 */
fun getDigitCount(number: Int): Int {
    return if (number == 0) {
        1
    } else {
        log10(abs(number).toDouble()).toInt() + 1
    }
}

/**
 * 1 → 1
 * 2 → 10
 * 3 → 100
 */
fun getMinNumberForDigits(digits: Int): Int {
    return if (digits <= 0) {
        throw IllegalArgumentException("桁数は1以上でなければなりません")
    } else {
        10.0.pow(digits - 1).toInt()
    }
}

/**
 * 上から指定した桁だけを残して切り下げ丸める
 * 例）
 *   digit = 3
 *   の場合
 *   43210 → 43000
 * @param digit
 */
fun roundDownToDigit(number: Int, digit: Int): Int {
    if (number == 0) return 0 // 0はそのまま返す

    val divisor = 10.0.pow(digit).toInt()
    return (number / divisor) * divisor // 最上位桁のみを残す
}

/**
 * 指定した桁切り上げ丸める
 *   例）
 *   digit = 3
 *   の場合
 *   43210 → 44000
 * @param digit
 */
fun roundUpToDigit(number: Int, digit: Int): Int {
    if (number == 0) return 0 // 0はそのまま返す

    val divisor = 10.0.pow(digit).toInt()
    return ceil(number / divisor.toDouble()).toInt() * divisor
}

/**
 * 下から指定した桁数の値だけを取得する
 */
fun getLowerDigits(number: Int, digit: Int): Int {
    if (number == 0) return 0 // 0はそのまま返す

    val digitCount = getDigitCount(number) // 桁数を取得
    if (digitCount <= digit) return number // 元の数値が2桁以下ならそのまま返す

    val divisor = 10.0.pow(digitCount - digit).toInt() // 最上位2桁を求める基準
    return number % divisor // 剰余を取ることで2桁以下を取得
}