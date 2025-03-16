package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreen
import com.ebata_shota.holdemstacktracker.ui.compose.util.SetWindowConfig
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TablePrepareActivity : ComponentActivity() {

    private val viewModel: TablePrepareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode: ThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isEnableDarkTheme: Boolean = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            SetWindowConfig(window, isEnableDarkTheme)
            HoldemStackTrackerTheme(
                darkTheme = isEnableDarkTheme
            ) {
                TablePrepareScreen(
                    navigateToBack = { finish() },
                    navigateToGameScreen = ::navigateToGameActivity
                )
            }
        }
    }

    private fun navigateToGameActivity(tableId: TableId) {
        val intent = GameActivity.intent(this, tableId)
        startActivity(intent)
        finish()
    }

    companion object {
        fun intent(
            context: Context,
            tableId: TableId,
        ) = Intent(context, TablePrepareActivity::class.java).apply {
            putExtras(
                TablePrepareViewModel.bundle(
                    tableId = tableId,
                )
            )
        }
    }
}