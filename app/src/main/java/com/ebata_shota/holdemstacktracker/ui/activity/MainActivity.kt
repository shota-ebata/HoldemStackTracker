package com.ebata_shota.holdemstacktracker.ui.activity

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.ThemeMode
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val resultJoinTableByQrActivity = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val tableId: TableId? = result.data?.let { intent ->
//                IntentCompat.getParcelableExtra(intent, TABLE_ID, TableId::class.java)
//            }
//            if (tableId != null) {
//                navigateToTableStandby(tableId)
//            }
//        }
//    }


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
        startActivity(intent)
    }

    private fun navigateToGame(tableId: TableId) {
        val intent = GameActivity.intent(
            context = this,
            tableId = tableId
        )
        startActivity(intent)
    }
}