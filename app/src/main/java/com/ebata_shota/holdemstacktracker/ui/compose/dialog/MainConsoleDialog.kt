package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableMainConsoleContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConsoleDialog(
    event: MainConsoleDialogEvent,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = event::onDismissRequestMainConsoleDialog,
        modifier = modifier,
    ) {
        Surface {
            TableMainConsoleContent(
                modifier = Modifier
                    .padding(16.dp),
                onClickTableCreator = event::onClickTableCreator,
                onClickJoinTableByQr = event::onClickJoinTableByQr,
                onClickJoinTableById = event::onClickJoinTableById,
            )
        }
    }
}

interface MainConsoleDialogEvent {
    fun onClickTableCreator()
    fun onClickJoinTableByQr()
    fun onClickJoinTableById()
    fun onDismissRequestMainConsoleDialog()
}