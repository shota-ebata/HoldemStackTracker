package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.usecase.HasErrorChipSizeTextValueUseCase
import javax.inject.Inject

class HasErrorChipSizeTextValueUseCaseImpl
@Inject
constructor() : HasErrorChipSizeTextValueUseCase {

    override fun invoke(value: String): Boolean {
        val intValue = value.toIntOrNull()
        return !(intValue != null && intValue > 0)
    }
}