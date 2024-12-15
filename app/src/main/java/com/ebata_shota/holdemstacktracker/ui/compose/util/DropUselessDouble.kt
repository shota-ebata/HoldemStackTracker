package com.ebata_shota.holdemstacktracker.ui.compose.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed


/**
 * 二重タップや同時押しなど
 * 連続で発生するイベントを抑制する
 * 名前は[dropUnlessResumed]から拝借
 *
 * 参考：https://zenn.dev/t2low/articles/4f96f32c919f27
 */
fun dropUselessDouble(
    delayState: DelayState,
    invoke: () -> Unit,
) {
    if (delayState.isDelayed) {
        delayState.isDelayed = false
        invoke()
    }
}

/**
 * Composeで直接使う場合
 */
@SuppressLint("ComposableNaming")
@Composable
fun dropUselessDouble(
    intervalTimeMillis: Long = 500L,
    delayState: DelayState = rememberDelayState(intervalTimeMillis = intervalTimeMillis),
    invoke: () -> Unit,
) {
    if (delayState.isDelayed) {
        delayState.isDelayed = false
        invoke()
    }
}
