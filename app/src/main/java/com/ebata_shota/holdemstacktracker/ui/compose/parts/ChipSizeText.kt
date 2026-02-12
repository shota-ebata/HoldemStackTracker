package com.ebata_shota.holdemstacktracker.ui.compose.parts

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.util.getChipString
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun ChipSizeText(
    textStringSource: StringSource,
    color: Color = Color.Unspecified,
    shouldShowBBSuffix: Boolean,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    suffixFontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    applyOutline: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val text = getChipString(
        textStringSource = textStringSource,
        shouldShowBBSuffix = shouldShowBBSuffix,
        suffixFontSize = suffixFontSize
    )

    if (applyOutline) {
        Box(modifier = modifier) {
            Text(
                text = text,
                style = style.copy(
                    color = Color.Black,
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 4f,
                        join = StrokeJoin.Round
                    )
                )
            )
            Text(
                text = text,
                style = style,
                color = color
            )
        }
    } else {
        Text(
            modifier = modifier,
            text = text,
            style = style,
            color = color
        )
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
private fun ChipSizeTextPreview() {
    HoldemStackTrackerTheme {
        ChipSizeText(
            textStringSource = StringSource("12.0"),
            shouldShowBBSuffix = true,
            style = MaterialTheme.typography.titleMedium,
            suffixFontSize = MaterialTheme.typography.bodySmall.fontSize,
        )
    }
}
