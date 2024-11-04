package com.ebata_shota.holdemstacktracker.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("com.ebata_shota.holdemstacktracker.app_pref")
            }
        )
    }

    @Provides
    @Singleton
    fun provideWorkerManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    /**
     * 参考： [https://medium.com/androiddevelopers/create-an-application-coroutinescope-using-hilt-dd444e721528]
     */
    @Provides
    @ApplicationScope
    @Singleton
    fun provideCoroutineScope(
        @CoroutineDispatcherIO dispatchersIO: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatchersIO)
    }

//    @Provides
//    @Singleton
//    fun provideFirebaseAnalytics(): FirebaseAnalytics {
//        return Firebase.analytics
//    }
}
