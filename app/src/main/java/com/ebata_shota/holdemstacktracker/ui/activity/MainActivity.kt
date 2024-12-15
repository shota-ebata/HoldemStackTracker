package com.ebata_shota.holdemstacktracker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            HoldemStackTrackerTheme {
                MainScreen(
                    navigateToTableCreator = ::navigateToTableCreator,
                    navigateToTableStandby = ::navigateToTableStandby,
                    navigateToGame = ::navigateToGame
                )
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