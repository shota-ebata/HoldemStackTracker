package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.BuildConfig
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import java.time.Instant
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameContentUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(4.dp)
        ) {
            // TOP
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                uiState.players
                    .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.TOP }
                    .forEach { playerUiState ->
                        GamePlayerCard(
                            uiState = playerUiState
                        )
                    }
            }
            Row(
                modifier = Modifier
                    .weight(weight = 1.0f)
            ) {
                // LEFT
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Left,
                    verticalArrangement = Arrangement.Center
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.LEFT }
                        .reversed()
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState
                            )
                        }
                }
                // CENTER
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    CenterPanelContent(
                        uiState = uiState.centerPanelContentUiState
                    )
                }
                // RIGHT
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    horizontalAlignment = AbsoluteAlignment.Right,
                    verticalArrangement = Arrangement.Center
                ) {
                    uiState.players
                        .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.RIGHT }
                        .forEach { playerUiState ->
                            GamePlayerCard(
                                uiState = playerUiState
                            )
                        }
                }
            }

            // BOTTOM
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                uiState.players
                    .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.BOTTOM }
                    .forEach { playerUiState ->
                        GamePlayerCard(
                            uiState = playerUiState
                        )
                    }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                ) {
                    val labelBorderWidth = 0.5.dp
                    Box(
                        modifier = Modifier
                            .border(
                                width = labelBorderWidth,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            )
                            .padding(
                                start = 8.dp,
                                top = 2.dp,
                                end = 4.dp,
                                bottom = 2.dp,
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.label_blind),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Box(
                        modifier = Modifier
                            .border(
                                width = labelBorderWidth,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            )
                            .padding(
                                start = 4.dp,
                                top = 2.dp,
                                end = 8.dp,
                                bottom = 2.dp,
                            ),
                    ) {
                        Text(
                            text = uiState.blindText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .heightIn(min = 56.dp),
                        onClick = {}
                    ) {
                        Text("Fold")
                    }
                    Button(
                        modifier = Modifier
                            .weight(2.0f)
                            .heightIn(min = 56.dp),
                        onClick = {}
                    ) {
                        Text("Check")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1.0f)
                            .heightIn(min = 56.dp),
                        onClick = {}
                    ) {
                        Text("AllIn")
                    }
                }
                var sliderPosition by remember { mutableFloatStateOf(100f) }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Call")
                            Text(text = "+1（=3）")
                        }
                    }
                    Button(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Raise")
                            Text(text = "+${round(sliderPosition).toInt()}（=${round(sliderPosition).toInt() + 2}）")
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {}
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "スタック"
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "arrowDropDown"
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.58f)
                    ) {
                        val interactionSource = remember { MutableInteractionSource() }
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            steps = 9,
                            valueRange = 0f..100f,
                            interactionSource = interactionSource,
                            thumb = {
                                Label(
                                    label = {
                                        PlainTooltip(
                                            modifier = Modifier
                                                .sizeIn(45.dp, 25.dp)
                                                .wrapContentWidth()
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = "スタック")
                                                Text(text = "${round(sliderPosition).toInt()}%")
                                            }
                                        }
                                    },
                                    interactionSource = interactionSource
                                ) {
                                    SliderDefaults.Thumb(
                                        interactionSource = interactionSource
                                    )
                                }
                            },
                            track = {
                                SliderDefaults.Track(
                                    sliderState = it
                                )
                            }
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(vertical = 4.dp),
                        onClick = {},
                        shape = ButtonDefaults.shape
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "${round(sliderPosition).toInt()}"
                        )
                    }
//                    IconButton(
//                        onClick = {}
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.tune_24),
//                            contentDescription = "tune"
//                        )
//                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "2 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "2.5 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "3 BB")
                    }
                    OutlinedButton(
                        modifier = Modifier
                            .weight(1.0f),
                        onClick = {}
                    ) {
                        Text(text = "4 BB")
                    }
                }
            }
        }
    }
}

data class GameContentUiState(
    val tableId: TableId,
    val game: Game,
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState,
    val blindText: String,
)

private class GameContentUiStatePreviewParam :
    PreviewParameterProvider<GameContentUiState> {
    override val values: Sequence<GameContentUiState> = sequenceOf(
        GameContentUiState(
            tableId = TableId("tableId"),
            game = Game(
                version = 0,
                appVersion = BuildConfig.VERSION_CODE.toLong(),
                players = emptySet(),
                podList = emptyList(),
                phaseList = emptyList(),
                updateTime = Instant.now()
            ),
            players = listOf(
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.LEFT,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.TOP,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = false
                ),
                GamePlayerUiState(
                    playerName = "PlayerName",
                    stack = "198",
                    playerPosition = GamePlayerUiState.PlayerPosition.RIGHT,
                    betSize = "2",
                    isLeaved = false,
                    isMine = false,
                    isCurrentPlayer = true
                )
            ),
            centerPanelContentUiState = CenterPanelContentUiState(
                betPhaseTextResId = R.string.label_pre_flop,
                totalPod = "0"
            ),
            blindText = "100/200",
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_6A,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_6A,
    name = "Dark Mode"
)
@Composable
private fun GameContentPreview(
    @PreviewParameter(GameContentUiStatePreviewParam::class)
    uiState: GameContentUiState
) {
    HoldemStackTrackerTheme {
        GameContent(
            uiState = uiState
        )
    }
}