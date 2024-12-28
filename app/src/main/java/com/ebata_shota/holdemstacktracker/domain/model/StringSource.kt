package com.ebata_shota.holdemstacktracker.domain.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

class StringSource
private constructor(
    @StringRes
    private val id: Int?,
    private val formatArgs: Array<Any>?,
    private val text: String?,
) {
    @Composable
    fun getString(): String = when {
        id != null -> {
            if (formatArgs != null) {
                stringResource(id, *formatArgs)
            } else {
                stringResource(id)
            }
        }

        text != null -> {
            text
        }

        else -> throw IllegalStateException("StringSourceがありえない状態")
    }

    companion object {

        operator fun invoke(
            @StringRes id: Int,
            vararg formatArgs: Any,
        ) = StringSource(
            id = id,
            formatArgs = formatArgs.toList().toTypedArray(),
            text = null
        )

        operator fun invoke(
            @StringRes id: Int,
        ) = StringSource(
            id = id,
            formatArgs = null,
            text = null
        )

        operator fun invoke(
            text: String,
        ) = StringSource(
            id = null,
            formatArgs = null,
            text = text
        )
    }
}