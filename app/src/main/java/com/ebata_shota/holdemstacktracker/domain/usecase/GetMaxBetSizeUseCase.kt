package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState

interface GetMaxBetSizeUseCase {
    fun invoke(actionStateList: List<ActionState>): Float
}