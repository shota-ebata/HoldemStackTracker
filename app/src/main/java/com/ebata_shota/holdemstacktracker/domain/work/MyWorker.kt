package com.ebata_shota.holdemstacktracker.domain.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MyWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    // FIXME: DIしたいのもをここに追加
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        Log.d("hoge", "kitaaaaa")
        return Result.success()
    }
}