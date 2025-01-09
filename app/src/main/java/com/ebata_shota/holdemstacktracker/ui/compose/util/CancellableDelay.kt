package com.ebata_shota.holdemstacktracker.ui.compose.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull


suspend fun cancellableDelay(
    delay: Long,
    cancelActionChannel: Channel<Unit>,
) = withContext(Dispatchers.Default) {
    val startTime = System.currentTimeMillis()
    while (true) {
        val elapsedTime = System.currentTimeMillis() - startTime
        if (elapsedTime >= delay) {
            // delayが終了
            break
        }
        val remainingTime = delay - elapsedTime
        // Channel確認のタイムアウト時間を確保
        val timeout = if (remainingTime > 100) {
            100
        } else {
            remainingTime
        }
        // 定期的にキャンセルアクションをチェック
        val cancelDelay = withTimeoutOrNull(timeout) {
            cancelActionChannel.tryReceive().isSuccess
        }
        if (cancelDelay == true) {
            break
        }
    }
}