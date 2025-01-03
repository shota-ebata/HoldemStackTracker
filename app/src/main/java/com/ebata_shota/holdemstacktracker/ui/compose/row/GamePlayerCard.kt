package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ChipSizeText
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth

@Composable
fun GamePlayerCard(
    uiState: GamePlayerUiState,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState.betTextPosition) {
        GamePlayerUiState.BetTextPosition.TOP -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BetSize(
                    uiState = uiState
                )
                Position(
                    uiState = uiState
                )
                PlayerCard(
                    uiState = uiState,
                    onClickCard = onClickCard,
                )
            }
        }

        GamePlayerUiState.BetTextPosition.BOTTOM -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerCard(
                    uiState = uiState,
                    onClickCard = onClickCard,
                )
                Position(
                    uiState = uiState
                )
                BetSize(
                    uiState = uiState,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }

}

@Composable
private fun Position(
    uiState: GamePlayerUiState,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (uiState.isBtn) {
            Icon(
                painter = painterResource(R.drawable.icon_btn),
                contentDescription = "icon_btn"
            )
        }
        if (uiState.positionLabelResId != null) {
            Box(
                modifier = Modifier
                    .border(
                        width = OutlineLabelBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(uiState.positionLabelResId))
            }
        }
        val lastActionText = uiState.lastActionText
        if (lastActionText != null) {
            Card(
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    text = lastActionText.getString()
                )
            }

        }
    }
}

@Composable
private fun BetSize(
    uiState: GamePlayerUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .heightIn(min = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (uiState.pendingBetSize != null) {
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(R.drawable.chip_icon),
                contentDescription = "chip"
            )
            ChipSizeText(
                modifier = Modifier
                    .padding(start = 8.dp),
                textStringSource = uiState.pendingBetSize,
                shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                style = MaterialTheme.typography.titleLarge,
                suffixFontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
    }
}

@Composable
private fun PlayerCard(
    uiState: GamePlayerUiState,
    onClickCard: () -> Unit,
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
        onClick = onClickCard,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 100.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChipSizeText(
                textStringSource = uiState.stack,
                shouldShowBBSuffix = uiState.shouldShowBBSuffix,
                style = MaterialTheme.typography.titleMedium,
                suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.playerName,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class GamePlayerUiState(
    val playerName: String,
    val stack: StringSource,
    val shouldShowBBSuffix: Boolean,
    val playerPosition: PlayerPosition,
    val pendingBetSize: StringSource?,
    val isLeaved: Boolean,
    val isMine: Boolean,
    val isCurrentPlayer: Boolean,
    val isBtn: Boolean,
    @StringRes
    val positionLabelResId: Int?,
    val lastActionText: StringSource?,
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
            stack = StringSource("198"),
            shouldShowBBSuffix = false,
            playerPosition = GamePlayerUiState.PlayerPosition.TOP,
            pendingBetSize = StringSource("2"),
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = false,
            isBtn = true,
            positionLabelResId = R.string.position_label_sb,
            lastActionText = StringSource(R.string.action_label_bet)
        ),
        GamePlayerUiState(
            playerName = "PlayerName",
            stack = StringSource("198"),
            shouldShowBBSuffix = false,
            playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
            pendingBetSize = StringSource("2"),
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = true,
            isBtn = false,
            positionLabelResId = R.string.position_label_bb,
            lastActionText = StringSource(R.string.action_label_bet)
        ),
        GamePlayerUiState(
            playerName = "Player123456789",
            stack = StringSource("200.0"),
            shouldShowBBSuffix = true,
            playerPosition = GamePlayerUiState.PlayerPosition.BOTTOM,
            pendingBetSize = null,
            isLeaved = false,
            isMine = false,
            isCurrentPlayer = true,
            isBtn = false,
            positionLabelResId = null,
            lastActionText = StringSource(R.string.action_label_bet)
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
            uiState = uiState,
            onClickCard = {}
        )
    }
}