package com.ebata_shota.holdemstacktracker.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope

@Composable
fun OnResumedEffect(
    block: suspend CoroutineScope.() -> Unit
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            block()
        }
    }
}