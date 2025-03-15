package com.ebata_shota.holdemstacktracker.domain.usecase

interface HasErrorChipSizeTextValueUseCase {
    fun invoke(value: String, range: IntRange): Boolean
}