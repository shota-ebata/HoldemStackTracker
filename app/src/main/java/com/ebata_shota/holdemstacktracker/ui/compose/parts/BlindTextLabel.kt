package com.ebata_shota.holdemstacktracker.ui.compose.parts

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun BlindTextLabel(
    blindText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                )
                .padding(
                    start = 8.dp,
                    top = 2.dp,
                    end = 4.dp,
                    bottom = 2.dp,
                ),
            text = stringResource(R.string.label_blind),
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                )
                .padding(
                    start = 4.dp,
                    top = 2.dp,
                    end = 8.dp,
                    bottom = 2.dp,
                ),
            text = blindText,
            color = MaterialTheme.colorScheme.onPrimary
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
private fun BlindTextLabelPreview() {
    HoldemStackTrackerTheme {
        BlindTextLabel(
            blindText = "1/2"
        )
    }
}