package com.ebata_shota.holdemstacktracker.ui.compose.parts

import android.content.res.Configuration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.theme.HoldemStackTrackerTheme

@Composable
fun RaiseSizeChangeButton(
    uiState: RaiseSizeChangeButtonUiState,
    onClickRaiseSizeButton: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
//        shape = RoundedCornerShape(8.dp),
        onClick = { onClickRaiseSizeButton(uiState.raiseSize) }
    ) {
        Text(
            text = uiState.labelStringSource.getString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

data class RaiseSizeChangeButtonUiState(
    val labelStringSource: StringSource,
    val raiseSize: Int,
)

@Preview(showBackground = true, showSystemUi = false, name = "Light Mode")
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun RaiseSizeChangeButtonPreview() {
    HoldemStackTrackerTheme {
        Surface {
            RaiseSizeChangeButton(
                uiState = RaiseSizeChangeButtonUiState(
                    labelStringSource = StringSource(id = R.string.position_label_sb),
                    raiseSize = 4
                ),
                onClickRaiseSizeButton = {},
            )
        }
    }
}