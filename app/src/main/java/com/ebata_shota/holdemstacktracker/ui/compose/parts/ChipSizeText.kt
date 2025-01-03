package com.ebata_shota.holdemstacktracker.ui.compose.parts

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.util.getChipString
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun ChipSizeText(
    textStringSource: StringSource,
    shouldShowBBSuffix: Boolean,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    suffixFontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    modifier: Modifier = Modifier,
) {
    val text = getChipString(
        textStringSource = textStringSource,
        shouldShowBBSuffix = shouldShowBBSuffix,
        suffixFontSize = suffixFontSize
    )
    Text(
        modifier = modifier,
        text = text,
        style = style
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