package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.usecase.GetDoubleToStringUseCase
import javax.inject.Inject

class GetDoubleToStringUseCaseImpl
@Inject
constructor() : GetDoubleToStringUseCase {
    override fun invoke(
        value: Double,
        betViewMode: BetViewMode
    ): String = when (betViewMode) {
        BetViewMode.Number -> value.toInt().toString() // TODO: 四捨五入したい
        BetViewMode.BB -> value.toString()// TODO: 小数点第一位で丸めたい
    }
}