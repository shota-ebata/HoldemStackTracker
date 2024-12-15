package com.ebata_shota.holdemstacktracker.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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