package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GmsBarcodeScannerRepositoryImpl
@Inject
constructor(
    private val gmsBarcodeScanner: GmsBarcodeScanner
) : GmsBarcodeScannerRepository {
    override suspend fun startQrScan(): String? = suspendCancellableCoroutine {
        gmsBarcodeScanner.startScan()
            .addOnSuccessListener { barcode ->
                it.resume(barcode.rawValue)
            }.addOnCanceledListener {
                it.resume(null)
            }.addOnFailureListener { e ->
                it.resume(null)
            }
    }
}