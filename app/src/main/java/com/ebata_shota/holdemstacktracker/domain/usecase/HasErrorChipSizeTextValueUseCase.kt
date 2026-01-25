package com.ebata_shota.holdemstacktracker.domain.usecase

fun interface HasErrorChipSizeTextValueUseCase {
    operator fun invoke(value: String, range: IntRange): Boolean
}
