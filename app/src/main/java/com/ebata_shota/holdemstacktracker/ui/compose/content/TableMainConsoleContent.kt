package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.compose.util.DelayState
import com.ebata_shota.holdemstacktracker.ui.compose.util.dropRedundantEvent
import com.ebata_shota.holdemstacktracker.ui.compose.util.rememberDelayState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun TableMainConsoleContent(
    onClickTableCreator: () -> Unit,
    onClickJoinTableByQr: () -> Unit,
    onClickJoinTableById: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val delayState: DelayState = rememberDelayState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.label_create_table),
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1.0f),
                    onClick = dropRedundantEvent(delayState = delayState) {
                        onClickTableCreator()
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "QR Scanner"
                        )
                        Text(
                            text = stringResource(R.string.button_create_table),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.label_join_table),
                style = MaterialTheme.typography.titleLarge,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1.0f),
                    onClick = dropRedundantEvent(delayState = delayState) {
                        onClickJoinTableByQr()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_qr_code_scanner_24),
                            contentDescription = "QR Scanner"
                        )
                        Text(
                            text = stringResource(R.string.button_qr_scanner),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                }
                ElevatedCard(
                    modifier = Modifier
                        .weight(1.0f),
                    onClick = dropRedundantEvent(delayState = delayState) {
                        onClickJoinTableById()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Table"
                        )
                        Text(
                            text = stringResource(R.string.button_table_id_search),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun TableMainConsoleContentPreview() {
    HoldemStackTrackerTheme {
        Surface {
            TableMainConsoleContent(
                onClickTableCreator = {},
                onClickJoinTableByQr = {},
                onClickJoinTableById = {}
            )
        }
    }
}