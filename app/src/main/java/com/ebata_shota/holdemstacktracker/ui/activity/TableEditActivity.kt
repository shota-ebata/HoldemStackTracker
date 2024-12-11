package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableEditScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableEditActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HoldemStackTrackerTheme {
                TableEditScreen(
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
        ) = Intent(context, TableEditActivity::class.java).apply {
            putExtras(TableEditViewModel.bundle(tableId))
        }
    }
}