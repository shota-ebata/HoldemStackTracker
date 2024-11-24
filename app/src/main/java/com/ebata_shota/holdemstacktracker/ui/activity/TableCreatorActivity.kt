package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.TableCreatorScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableCreatorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // FIXME: 対応する

        setContent {
            TableCreatorScreen(
                navigateToGame = ::navigateToGame
            )
        }
    }

    private fun navigateToGame(tableId: TableId) {
        val intent = TableStandbyActivity.intent(this, tableId)
        startActivity(intent)
    }

    companion object {
        fun intent(context: Context) = Intent(context, TableCreatorActivity::class.java)
    }
}