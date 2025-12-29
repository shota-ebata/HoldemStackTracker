package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetMyPlayerIdUseCase {
    suspend fun invoke(): PlayerId?
}