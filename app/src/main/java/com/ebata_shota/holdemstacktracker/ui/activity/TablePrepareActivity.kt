package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TablePrepareScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TablePrepareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TablePrepareActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoldemStackTrackerTheme {
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
            tableId: TableId
        ) = Intent(context, TablePrepareActivity::class.java).apply {
            putExtras(TablePrepareViewModel.bundle(tableId))
        }
    }
}