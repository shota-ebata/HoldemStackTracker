package com.ebata_shota.holdemstacktracker.ui.compose.content

import android.content.res.Configuration
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerCard
import com.ebata_shota.holdemstacktracker.ui.compose.row.GamePlayerUiState
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import kotlin.math.cos

@Composable
fun PlayersContent(
    uiState: GameMainPanelUiState,
    onClickCenterPanel: () -> Unit,
    onClickPlayerCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val leftPlayers = uiState.players.filter { it.playerPosition == GamePlayerUiState.PlayerPosition.LEFT }
    val topPlayers = uiState.players.filter { it.playerPosition== GamePlayerUiState.PlayerPosition.TOP }
    val rightPlayers = uiState.players.filter { it.playerPosition == GamePlayerUiState.PlayerPosition.RIGHT }

    val leftArrangement = if (leftPlayers.count() <= 1) Arrangement.Center else Arrangement.SpaceEvenly
    val rightArrangement = if (rightPlayers.count() <= 1) Arrangement.Center else Arrangement.SpaceEvenly

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = 8f
                translationY = -100f
            },
    ) {
        // Outer border/railing
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF003300), // Very dark green for the border
            shape = RoundedCornerShape(percent = 50)
        ) {}

        // Inner playing surface (felt)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding creates the border effect
            color = Color(0xFF006400), // The original dark green
            shape = RoundedCornerShape(percent = 50)
        ) {}


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // TOP
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                if (topPlayers.isEmpty()) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
                topPlayers
                    .forEach { playerUiState ->
                        GamePlayerCard(
                            uiState = playerUiState,
                        )
                    }
            }
            Row(
                modifier = Modifier
                    .weight(weight = 1.0f)
            ) {
                // LEFT
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = if (leftPlayers.count() == 3) 0.dp else 80.dp)
                        .weight(1.0f),
                    horizontalAlignment = AbsoluteAlignment.Left,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        horizontalAlignment = AbsoluteAlignment.Left,
                        verticalArrangement = leftArrangement,
                    ) {
                        leftPlayers
                            .reversed()
                            .forEach { playerUiState ->
                                GamePlayerCard(
                                    uiState = playerUiState,
                                )
                            }
                    }
                }
                // CENTER
                Column(
                    modifier = Modifier.weight(1.0f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CenterPanelContent(
                            uiState = uiState.centerPanelContentUiState,
                            onClickCenterPanel = onClickCenterPanel
                        )
                    }
                    // BOTTOM
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        uiState.players
                            .filter { it.playerPosition == GamePlayerUiState.PlayerPosition.BOTTOM }
                            .forEach { playerUiState ->
                                GamePlayerCard(
                                    uiState = playerUiState,
                                    modifier = Modifier.graphicsLayer {
                                        // Counter-rotate to make it flat
                                        rotationX = -8f
                                        // Correct the perspective distortion
                                        val angleRad = Math.toRadians(30.0).toFloat()
                                        scaleY = cos(angleRad)
                                    }
                                )
                            }
                    }
                }
                // RIGHT
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = if (rightPlayers.count() == 3) 0.dp else 80.dp)
                        .weight(1.0f),
                    horizontalAlignment = AbsoluteAlignment.Right,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        horizontalAlignment = AbsoluteAlignment.Right,
                        verticalArrangement = rightArrangement,
                    ) {
                        rightPlayers
                            .forEach { playerUiState ->
                                GamePlayerCard(
                                    uiState = playerUiState,
                                )
                            }
                    }
                }
            }
        }
    }
}

data class GameMainPanelUiState(
    val players: List<GamePlayerUiState>,
    val centerPanelContentUiState: CenterPanelContentUiState,
)


@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
    device = "spec:width=411dp,height=491dp"
)
@Preview(
    showBackground = false,
    showSystemUi = false,
    name = "Light Mode",
    device = "spec:width=411dp,height=491dp"
)
@Composable
private fun PlayersContentPreview(
    @PreviewParameter(PlayersContentPreviewParameterProvider::class)
    uiState: GameMainPanelUiState,
) {
    HoldemStackTrackerTheme {
        PlayersContent(
            uiState = uiState,
            onClickCenterPanel = {},
            onClickPlayerCard = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}