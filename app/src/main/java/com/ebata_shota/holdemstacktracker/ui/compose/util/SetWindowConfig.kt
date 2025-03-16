package com.ebata_shota.holdemstacktracker.ui.compose.util

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetWindowConfig(window: Window, isEnableDarkTheme: Boolean) {
    LaunchedEffect(isEnableDarkTheme) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isEnableDarkTheme
            isAppearanceLightNavigationBars = !isEnableDarkTheme
        }
    }
}