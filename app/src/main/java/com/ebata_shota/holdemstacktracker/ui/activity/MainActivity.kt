package com.ebata_shota.holdemstacktracker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.screen.MainScreen
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            HoldemStackTrackerTheme {
                MainScreen(
                    navigateToNextScreen = {
                        navigateToNextActivity()
                    }
                )
            }
        }
    }

    private fun navigateToNextActivity() {
        // TODO: startActivity
    }



    private fun navigateToTableCreator() {
        val intent = TableCreatorActivity.intent(context = this)
        startActivity(intent)
    }

    private fun navigateToTableStandby() {
        val intent = TableStandbyActivity.intent(
            context = this,
            tableId = TableId("625885eb-20f6-459f-a8a9-edfb4bbd2a3f")
        )
        startActivity(intent)
    }
}