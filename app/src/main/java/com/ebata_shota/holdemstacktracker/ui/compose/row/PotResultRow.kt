package com.ebata_shota.holdemstacktracker.ui.compose.row

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.parts.ChipSizeText
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme
import com.ebata_shota.holdemstacktracker.ui.theme.OutlineLabelBorderWidth

@Composable
fun PotResultRow(
    uiState: PotResultRowUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

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
                text = uiState.potLabelText.getString(),
                style = MaterialTheme.typography.bodySmall,
            )
            ChipSizeText(
                textStringSource = uiState.potSizeText,
                shouldShowBBSuffix = false,
                style = MaterialTheme.typography.titleLarge,
                suffixFontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "delete"
        )
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
            uiState.winnerPlayerNames.forEach {
                Text(
                    text = it.getString(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

data class PotResultRowUiState(
    val potLabelText: StringSource,
    val potSizeText: StringSource,
    val winnerPlayerNames: List<StringSource>,
)

private class PotResultRowPreviewParam :
    PreviewParameterProvider<List<StringSource>> {
    override val values: Sequence<List<StringSource>> = sequenceOf(
        listOf(
            StringSource("櫻木")
        ),
        listOf(
            StringSource("櫻木"),
            StringSource("風野"),
        ),
        listOf(
            StringSource("櫻木"),
            StringSource("風野"),
            StringSource("八宮"),
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
private fun PotResultRowPreview(
    @PreviewParameter(PotResultRowPreviewParam::class)
    winnerPlayerNames: List<StringSource>,
) {
    HoldemStackTrackerTheme {
        Surface {
            PotResultRow(
                uiState = PotResultRowUiState(
                    potLabelText = StringSource("Main Pot"),
                    potSizeText = StringSource("2000"),
                    winnerPlayerNames = winnerPlayerNames
                )
            )
        }
    }
}