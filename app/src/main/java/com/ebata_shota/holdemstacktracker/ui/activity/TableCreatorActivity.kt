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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorScreen
import com.ebata_shota.holdemstacktracker.ui.compose.util.SetWindowConfig
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableCreatorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TableCreatorActivity : ComponentActivity() {

    private val viewModel: TableCreatorViewModel by viewModels()

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
                TableCreatorScreen()
            }
        }
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    launch {
                        viewModel.navigateEvent.collect {
                            when (it) {
                                is TableCreatorViewModel.NavigateEvent.Back -> finish()
                                is TableCreatorViewModel.NavigateEvent.TablePrepare -> navigateToGame(it.tableId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToGame(tableId: TableId) {
        val intent = TablePrepareActivity.intent(this, tableId)
        startActivity(intent)
        finish()
    }

    companion object {
        fun intent(context: Context) = Intent(context, TableCreatorActivity::class.java)
    }
}