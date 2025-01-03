package com.ebata_shota.holdemstacktracker.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource

@Composable
fun getChipString(
    textStringSource: StringSource,
    shouldShowBBSuffix: Boolean,
    suffixFontSize: TextUnit,
): AnnotatedString {
    val text = buildAnnotatedString {
        append(textStringSource.getString())
        if (shouldShowBBSuffix) {
            pushStyle(
                SpanStyle(
                    fontSize = suffixFontSize,
                )
            )
            append(" ")
            append(stringResource(R.string.label_bb))
        }
    }
    return text
}