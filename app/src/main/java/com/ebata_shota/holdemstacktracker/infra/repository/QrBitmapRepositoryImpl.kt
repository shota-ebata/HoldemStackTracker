package com.ebata_shota.holdemstacktracker.infra.repository

import android.graphics.Bitmap
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QrBitmapRepositoryImpl
@Inject
constructor(
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : QrBitmapRepository {

    override suspend fun createQrBitmap(
        text: String
    ): Bitmap = withContext(dispatcher) {
        BarcodeEncoder().encodeBitmap(
            text,
            BarcodeFormat.QR_CODE,
            400,
            400
        )
    }
}