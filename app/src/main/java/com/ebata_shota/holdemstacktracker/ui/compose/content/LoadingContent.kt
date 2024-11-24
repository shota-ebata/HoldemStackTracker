package com.ebata_shota.holdemstacktracker.ui.compose.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview(showBackground = true)
fun LoadingContentPreview() {
    HoldemStackTrackerTheme {
        LoadingContent()
    }
}