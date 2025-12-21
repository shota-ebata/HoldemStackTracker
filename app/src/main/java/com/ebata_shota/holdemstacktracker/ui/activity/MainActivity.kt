package com.ebata_shota.holdemstacktracker.ui.activity

import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val prepareTableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == TablePrepareActivity.RESULT_CODE_BAN_FINISH) {
            val isKicked = result.data?.getBooleanExtra(
                    TablePrepareActivity.EXTRA_BAN_MESSAGE,
                    false
                ) ?: false
            viewModel.onResultNavigation(isKicked)
        }
    }


    private val viewModel: MainViewModel by viewModels()

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
                MainScreen(
                    navigateToTableCreator = ::navigateToTableCreator,
                    navigateToTableStandby = ::navigateToTableStandby,
                    navigateToGame = ::navigateToGame,
                    closeApp = { finish() },
                )
            }
        }

        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    launch {
                        viewModel.toastEvent.collect {
                            Toast.makeText(
                                this@MainActivity, getString(R.string.toast_message_kicked),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

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

    private fun navigateToTableCreator() {
        val intent = TableCreatorActivity.intent(context = this)
        startActivity(intent)
    }

    private fun navigateToTableStandby(tableId: TableId) {
        val intent = TablePrepareActivity.intent(
            context = this,
            tableId = tableId
        )
        prepareTableLauncher.launch(intent)
    }

    private fun navigateToGame(tableId: TableId) {
        val intent = GameActivity.intent(
            context = this,
            tableId = tableId
        )
        startActivity(intent)
    }
}