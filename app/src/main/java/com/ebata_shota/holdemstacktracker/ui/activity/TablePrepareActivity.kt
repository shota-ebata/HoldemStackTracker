package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
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
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreen
import com.ebata_shota.holdemstacktracker.ui.compose.util.SetWindowConfig
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel.Navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TablePrepareActivity : ComponentActivity() {

    private val viewModel: TablePrepareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(owner = this,
            onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            }
        )
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.navigateEvent.collect {
                        when (it) {
                            is Navigate.Finish -> finish()
                            is Navigate.KickedFinish -> finishWithKicked()
                            is Navigate.Game -> navigateToGameActivity(it.tableId)
                        }
                    }
                }
            }

            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onResumed()
                }
            }
        }
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
                TablePrepareScreen()
            }
        }
    }

    private fun navigateToGameActivity(tableId: TableId) {
        val intent = GameActivity.intent(this, tableId)
        startActivity(intent)
        finish()
    }

    private fun finishWithKicked() {
        val resultIntent = Intent().apply {
            putExtra(TablePrepareActivity.Companion.EXTRA_BAN_MESSAGE, true)
        }
        setResult(TablePrepareActivity.Companion.RESULT_CODE_BAN_FINISH, resultIntent)
        finish()
    }

    companion object {
        const val RESULT_CODE_BAN_FINISH = RESULT_FIRST_USER + 1
        const val EXTRA_BAN_MESSAGE = "com.ebata_shota.holdemstacktracker.ui.activity.EXTRA_BAN_MESSAGE"

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