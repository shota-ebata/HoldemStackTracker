package com.ebata_shota.holdemstacktracker.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.viewmodel.TableStandbyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableStandbyActivity : ComponentActivity() {

    private val viewModel: TableStandbyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        setContent {

        }
    }
    companion object {
        fun intent(
            context: Context,
            tableId: TableId
        ) = Intent(context, TableStandbyActivity::class.java).apply {
            putExtras(TableStandbyViewModel.bundle(tableId))
        }
    }
}