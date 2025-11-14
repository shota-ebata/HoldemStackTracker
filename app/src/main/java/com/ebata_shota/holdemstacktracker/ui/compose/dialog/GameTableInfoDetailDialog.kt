package com.ebata_shota.holdemstacktracker.ui.compose.dialog

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameTableInfoDetailContent
import com.ebata_shota.holdemstacktracker.ui.compose.content.GameTableInfoDetailContentUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTableInfoDetailDialog(
    uiState: GameTableInfoDetailContentUiState,
    getTableQrPainter: () -> Painter?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Surface {
            GameTableInfoDetailContent(
                uiState = uiState,
                getTableQrPainter = getTableQrPainter,
            )
        }
    }
}
