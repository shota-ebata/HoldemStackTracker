package com.ebata_shota.holdemstacktracker.ui.compose.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow


@Composable
@SuppressLint("ComposableNaming")
inline fun <reified T> SharedFlow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            this@collectWithLifecycle.collect(collector)
        }
    }
}