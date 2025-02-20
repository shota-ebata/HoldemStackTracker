package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.GameScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isKeepScreenOn.collect {
                    if (it) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }
        }
        setContent {
            HoldemStackTrackerTheme {
                GameScreen(navigateTo = ::navigateTo)
            }
        }
    }

    private fun navigateTo(
        navigate: GameViewModel.Navigate
    ) {
        when (navigate) {
            is GameViewModel.Navigate.TablePrepare -> {
                val intent = TablePrepareActivity.intent(
                    context = this,
                    tableId = navigate.tableId,
                )
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        fun intent(context: Context, tableId: TableId) =
            Intent(context, GameActivity::class.java).apply {
                putExtras(GameViewModel.bundle(tableId))
            }
    }
}