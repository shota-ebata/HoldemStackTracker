package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun GamePlayerCard(
    uiState: GamePlayerUiState,
    modifier: Modifier = Modifier
) {
    when (uiState.betTextPosition) {
        GamePlayerUiState.BetTextPosition.TOP -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BetSize(
                    betSize = uiState.pendingBetSize
                )
                PlayerCard(
                    uiState = uiState
                )
            }
        }

        GamePlayerUiState.BetTextPosition.BOTTOM -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerCard(
                    uiState = uiState
                )
                BetSize(
                    betSize = uiState.pendingBetSize
                )
            }
        }
    }

}

@Composable
private fun BetSize(betSize: String?) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .heightIn(min = 24.dp)
    ) {
        if (betSize != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(R.drawable.chip_icon),
                contentDescription = "chip"
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = betSize
            )
        }
    }
}

@Composable
private fun PlayerCard(
    uiState: GamePlayerUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.isCurrentPlayer) {
                MaterialTheme.colorScheme.inversePrimary
            } else {
                Color.Unspecified
            }
        ),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 100.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                if (uiState.isBtn) {
                    Icon(
                        painter = painterResource(R.drawable.icon_btn),
                        contentDescription = "icon_btn"
                    )
                }
                Text(
                    text = uiState.playerName,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1

                )
            }
            Text(text = uiState.stack)
        }
    }
}

data class GamePlayerUiState(
    val playerName: String,
    val stack: String,
    val playerPosition: PlayerPosition,
    val pendingBetSize: String?,
    val isLeaved: Boolean,
    val isMine: Boolean,
    val isCurrentPlayer: Boolean,
    val isBtn: Boolean,
) {
    val betTextPosition: BetTextPosition = when (playerPosition) {
        PlayerPosition.BOTTOM -> BetTextPosition.TOP
        else -> BetTextPosition.BOTTOM
    }
    enum class BetTextPosition {
        TOP,
        BOTTOM
    }

    enum class PlayerPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }
}

private class GamePlayerCardPreviewParam :
    PreviewParameterProvider<GamePlayerUiState> {
    override val values: Sequence<GamePlayerUiState> = sequenceOf(
        GamePlayerUiState(
            playerName = "PlayerName",
            stack = "198",
            playerPosition = GamePlayerUiState.PlayerPosition.TOP,
            pendingBetSize = "2",
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = false,
            isBtn = true
        ),
        GamePlayerUiState(
            playerName = "PlayerName",
            stack = "198",
            playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
            pendingBetSize = "2",
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = true,
            isBtn = false
        ),
        GamePlayerUiState(
            playerName = "Player123456789",
            stack = "200",
            playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
            pendingBetSize = null,
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = true,
            isBtn = false
        )
    )
}

@Preview(showBackground = false, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = false,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun GamePlayerCardPreview(
    @PreviewParameter(GamePlayerCardPreviewParam::class)
    uiState: GamePlayerUiState
) {
    HoldemStackTrackerTheme {
        GamePlayerCard(
            uiState = uiState
        )
    }
}