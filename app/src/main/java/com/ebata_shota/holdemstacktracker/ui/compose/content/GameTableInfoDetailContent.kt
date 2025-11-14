package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PotId
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ChipSizeText
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth

@Composable
fun GameTableInfoDetailContent(
    uiState: GameTableInfoDetailContentUiState,
    getTableQrPainter: () -> Painter?,
    modifier: Modifier = Modifier,
) {
    val qrPainter = getTableQrPainter()
    val clipboardManager = LocalClipboardManager.current
    Surface(
        modifier = modifier
    )  {
        Column(
            modifier = modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            qrPainter?.let {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = it,
                        contentDescription = "",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                }

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = uiState.tableIdText.getString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    IconButton(
                        modifier = Modifier
                            .size(48.dp),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.tableId.value))
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_content_copy_24),
                            contentDescription = "copy"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = stringResource(R.string.label_blind),
                    style = MaterialTheme.typography.bodySmall
                )
                Box(
                    modifier = Modifier
                        .height(height = 16.dp)
                        .width(OutlineLabelBorderWidth)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "100/200",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp),
                text = "River",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.potList.forEach {
                    Column(
                        modifier = Modifier
                            .border(
                                width = OutlineLabelBorderWidth,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it.potName.getString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                        ChipSizeText(
                            textStringSource = it.potSize,
                            shouldShowBBSuffix = false,
                            style = MaterialTheme.typography.titleLarge,
                            suffixFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_bet),
                    style = MaterialTheme.typography.bodySmall
                )
                ChipSizeText(
                    textStringSource = uiState.pendingTotalBetSize,
                    shouldShowBBSuffix = false,
                    style = MaterialTheme.typography.titleLarge,
                    suffixFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            }
        }
    }
}

data class GameTableInfoDetailContentUiState(
    val tableId: TableId,
    val blindText: StringSource,
    val potList: List<Pot>,
    val pendingTotalBetSize: StringSource
) {
    val tableIdText = StringSource(R.string.table_id_prefix, tableId.value)

    data class Pot(
        val id: PotId,
        val potName: StringSource,
        val potSize: StringSource,
    )
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun GameTableInfoDetailContentPreview() {
    val painter = painterResource(R.drawable.baseline_qr_code_2_24)
    HoldemStackTrackerTheme {
        GameTableInfoDetailContent(
            uiState = GameTableInfoDetailContentUiState(
                tableId = TableId("bf2086"),
                blindText = StringSource("100/200"),
                potList = listOf(
                    GameTableInfoDetailContentUiState.Pot(
                        id = PotId("pot1"),
                        potName = StringSource("Main Pot"),
                        potSize = StringSource("2000"),
                    ),
                    GameTableInfoDetailContentUiState.Pot(
                        id = PotId("pot2"),
                        potName = StringSource("Side Pot 1"),
                        potSize = StringSource("1500"),
                    ),
                ),
                pendingTotalBetSize = StringSource("500")
            ),
            getTableQrPainter = { painter }
        )
    }
}