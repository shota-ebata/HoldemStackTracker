package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetMyPlayerIdUseCase {
    suspend operator fun invoke(): PlayerId?
}
