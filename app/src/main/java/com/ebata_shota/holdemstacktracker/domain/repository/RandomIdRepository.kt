package com.ebata_shota.holdemstacktracker.domain.repository

interface RandomIdRepository {
    fun generateRandomId(): String
}