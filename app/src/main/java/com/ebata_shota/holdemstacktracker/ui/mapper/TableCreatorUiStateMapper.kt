package com.ebata_shota.holdemstacktracker.ui.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.GameType
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.StringSource
import com.ebata_shota.holdemstacktracker.ui.compose.content.TableCreatorContentUiState
import com.ebata_shota.holdemstacktracker.ui.compose.parts.TextFieldErrorUiState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class TableCreatorUiStateMapper
@Inject
constructor() {

    fun createUiState(
        ringGameRule: Rule.RingGame,
        submitButtonLabel: StringSource,
    ) = TableCreatorContentUiState(
        gameType = GameType.RingGame,
        sbSize = TextFieldErrorUiState(
            label = R.string.sb_size_label,
            value = TextFieldValue(
                "%,d".format(ringGameRule.sbSize)
            )
        ),
        bbSize = TextFieldErrorUiState(
            label = R.string.bb_size_label,
            value = TextFieldValue(
                "%,d".format(ringGameRule.bbSize)
            ),
        ),
        defaultStack = TextFieldErrorUiState(
            label = R.string.default_stack_label,
            value = TextFieldValue(
                ringGameRule.defaultStack.toString()
            )
        ),
        submitButtonLabel = submitButtonLabel,
        bottomErrorMessage = null
    )
}