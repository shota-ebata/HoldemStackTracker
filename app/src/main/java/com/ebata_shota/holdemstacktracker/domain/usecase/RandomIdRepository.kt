package com.ebata_shota.holdemstacktracker.domain.usecase

interface RandomIdRepository {
    fun generateRandomPlayerId(): String
}