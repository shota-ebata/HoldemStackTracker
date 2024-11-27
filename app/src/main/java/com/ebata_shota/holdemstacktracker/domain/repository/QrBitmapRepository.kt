package com.ebata_shota.holdemstacktracker.domain.repository

import android.graphics.Bitmap

interface QrBitmapRepository {
    suspend fun createQrBitmap(text: String): Bitmap
}