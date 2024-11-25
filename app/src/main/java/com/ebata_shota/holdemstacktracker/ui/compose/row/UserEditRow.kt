package com.ebata_shota.holdemstacktracker.ui.compose.row

import androidx.compose.foundation.Image
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
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

        Text(
            text = uiState.stackSize,
            modifier = Modifier
                .weight(1.0f)
                .wrapContentSize(Alignment.Center)
                .padding(vertical = 4.dp),
        )

        if (uiState.isEditable) {
            Box(
                modifier = Modifier
                    .clickable {
                        onClickStackEditButton()
                    }
                    .size(48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Spacer(
                modifier = Modifier.width(48.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            if (uiState.isEditable) {

                Column(
                    modifier = Modifier
                ) {
                    Box(
                        modifier = Modifier
                            .clickable { onClickUpButton() }
                            .size(width = 48.dp, height = 30.dp)
                            .padding(bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_up_24),
                            contentDescription = "",
                            modifier = Modifier
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
                            .size(width = 48.dp, height = 30.dp)
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                            contentDescription = "",
                            modifier = Modifier
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

@Composable
@Preview(showBackground = true)
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