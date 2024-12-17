package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
                    betSize = uiState.betSize
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
                    betSize = uiState.betSize
                )
            }
        }
    }

}

@Composable
private fun BetSize(betSize: String) {
    // FIXME: 自分が用意したベクター画像に置き換える
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .padding(horizontal = 8.dp)
    ) {
        Image(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(R.drawable.chip),
            contentDescription = "chip"
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp),
            text = betSize
        )
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
        )
    ) {
        Column(
            modifier = Modifier.widthIn(min = 100.dp).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = uiState.playerName)
            Text(text = uiState.stack)
        }
    }
}

data class GamePlayerUiState(
    val playerName: String,
    val stack: String,
    val playerPosition: PlayerPosition,
    val betSize: String,
    val isLeaved: Boolean,
    val isMine: Boolean,
    val isCurrentPlayer: Boolean
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
            betSize = "2",
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = false
        ),
        GamePlayerUiState(
            playerName = "PlayerName",
            stack = "198",
            playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
            betSize = "2",
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = true
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