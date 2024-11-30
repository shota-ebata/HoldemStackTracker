package com.ebata_shota.holdemstacktracker.domain.repository

interface GmsBarcodeScannerRepository {
    suspend fun startQrScan(): String?
}