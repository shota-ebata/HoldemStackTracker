package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth
import com.ebata_shota.holdemstacktracker.ui.theme.SideSpace

@Composable
fun UserEditRow(
    uiState: PlayerEditRowUiState,
    onClickPlayerEditButton: () -> Unit,
    onClickUpButton: () -> Unit,
    onClickDownButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(start = SideSpace)
                .padding(vertical = 4.dp)
        ) {
            if (uiState.isLeaved) {
                Box(
                    modifier = Modifier
                        .border(
                            width = OutlineLabelBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.label_is_leaved),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                text = uiState.playerName,
                style = MaterialTheme.typography.titleMedium
            )
        }


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
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            if (uiState.isEditable) {
                Column(
                    modifier = Modifier
                        ,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onClickUpButton()
                                }
                            )
                            .size(width = 48.dp, height = 32.dp),
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
                            .clickable(
                                onClick = {
                                    onClickDownButton()
                                }
                            )
                            .size(width = 48.dp, height = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "down"
                        )
                    }
                }
                IconButton(
                    modifier = Modifier
                        .padding(end = SideSpace)
                        .size(48.dp),
                    onClick = {
                        onClickPlayerEditButton()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "edit"
                    )
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
    val isLeaved: Boolean,
    val isEditable: Boolean,
)

private class UserEditPreviewParam : PreviewParameterProvider<PlayerEditRowUiState> {
    override val values: Sequence<PlayerEditRowUiState> = sequenceOf(
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = "10000",
            isLeaved = true,
            isEditable = false
        ),
        PlayerEditRowUiState(
            playerId = PlayerId("playerId1"),
            playerName = "playerName12345",
            stackSize = "10000",
            isLeaved = true,
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
    @PreviewParameter(UserEditPreviewParam::class)
    uiState: PlayerEditRowUiState
) {
    HoldemStackTrackerTheme {
        UserEditRow(
            uiState = uiState,
            onClickPlayerEditButton = {},
            onClickUpButton = {},
            onClickDownButton = {}
        )
    }
}