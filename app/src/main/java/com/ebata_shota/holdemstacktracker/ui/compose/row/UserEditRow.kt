package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun UserEditRow(
    uiState: PlayerEditRowUiState,
    onClickStackEditButton: () -> Unit,
    onClickUpButton: () -> Unit,
    onClickDownButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(56.dp)
            .padding(
                start = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = uiState.playerName,
            modifier = Modifier
                .weight(1.0f)
        )

        Row(
            modifier = Modifier
                .weight(1.0f)
                .wrapContentSize(Alignment.Center)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.stackSize,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center),
            )

            if (uiState.isEditable) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onClickStackEditButton()
                        }
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "edit",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier.width(48.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            if (uiState.isEditable) {

                Column(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { onClickUpButton() }
                            .size(width = 48.dp, height = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "up"
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .width(48.dp)
                            .padding(horizontal = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clickable { onClickDownButton() }
                            .size(width = 48.dp, height = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "down"
                        )
                    }
                }
            } else {
                Spacer(
                    modifier = Modifier.width(48.dp)
                )
            }
        }
    }
}

data class PlayerEditRowUiState(
    val playerId: PlayerId,
    val playerName: String,
    val stackSize: String,
    val isEditable: Boolean
)

private class PreviewParam : PreviewParameterProvider<PlayerEditRowUiState> {
    override val values: Sequence<PlayerEditRowUiState> = sequenceOf(
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = "10000",
            isEditable = false
        ),
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = "10000",
            isEditable = true
        ),
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
fun UserEditPreview(
    @PreviewParameter(PreviewParam::class)
    uiState: PlayerEditRowUiState
) {
    HoldemStackTrackerTheme {
        UserEditRow(
            uiState = uiState,
            onClickStackEditButton = {},
            onClickUpButton = {},
            onClickDownButton = {}
        )
    }
}