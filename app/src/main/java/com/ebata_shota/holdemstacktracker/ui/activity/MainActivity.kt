package com.ebata_shota.holdemstacktracker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.IntentCompat
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.ebata_shota.holdemstacktracker.ui.activity.JoinTableByQrActivity.Companion.TABLE_ID
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var gmsBarcodeScannerRepository: GmsBarcodeScannerRepository

    private val resultJoinTableByQrActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val tableId: TableId? = result.data?.let { intent ->
                IntentCompat.getParcelableExtra(intent, TABLE_ID, TableId::class.java)
            }
            if (tableId != null) {
                navigateToTableStandby(tableId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            HoldemStackTrackerTheme {
                MainScreen(
                    navigateToTableCreator = ::navigateToTableCreator,
                    navigateToTableStandby = ::navigateToTableStandby,
                    navigateToJoinTableByQrActivity = ::navigateToJoinTableByQrActivity
                )
            }
        }
    }

    private fun navigateToTableCreator() {
        val intent = TableCreatorActivity.intent(context = this)
        startActivity(intent)
    }

    private fun navigateToTableStandby(tableId: TableId = TableId("33698e51-9cd4-4dac-a556-10455b43164e")) {
        val intent = TableEditActivity.intent(
            context = this,
            tableId = tableId
        )
        startActivity(intent)
    }

    private fun navigateToJoinTableByQrActivity() {
        val intent = JoinTableByQrActivity.intent(
            context = this
        )
        resultJoinTableByQrActivity.launch(intent)
    }
}