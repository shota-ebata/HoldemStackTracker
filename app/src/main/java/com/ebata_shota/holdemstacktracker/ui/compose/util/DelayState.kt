package com.ebata_shota.holdemstacktracker.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.dropUnlessResumed
import kotlinx.coroutines.delay

/**
 * 二重タップや同時押しなど
 * 連続で発生するイベントを抑制することを主目的としたDelay状態
 *
 * 参考：https://zenn.dev/t2low/articles/4f96f32c919f27
 */
data class DelayState(
    val delayTimeMillis: Long = 500,
) {
    var isDelayed: Boolean by mutableStateOf(true)
}

@Composable
fun rememberDelayState(
    intervalTimeMillis: Long = 500,
): DelayState {
    val state = remember { DelayState(delayTimeMillis = intervalTimeMillis) }
    LaunchedEffect(state.isDelayed) {
        if (state.isDelayed) return@LaunchedEffect
        delay(state.delayTimeMillis)
        state.isDelayed = true
    }
    return state
}

/**
 * 二重タップや同時押しなど
 * 連続で発生するイベントを抑制する
 * 名前は[dropUnlessResumed]から拝借
 *
 * 参考：https://zenn.dev/t2low/articles/4f96f32c919f27
 */
fun dropRedundantEventWith(
    delayState: DelayState,
    block: () -> Unit,
) {
    if (delayState.isDelayed) {
        delayState.isDelayed = false
        block()
    }
}

/**
 * Composeで直接使う場合
 */
@Composable
fun dropRedundantEvent(
    intervalTimeMillis: Long = 500,
    delayState: DelayState = rememberDelayState(intervalTimeMillis = intervalTimeMillis),
    block: () -> Unit,
): () -> Unit {
    return {
        if (delayState.isDelayed) {
            delayState.isDelayed = false
            block()
        }
    }
}
