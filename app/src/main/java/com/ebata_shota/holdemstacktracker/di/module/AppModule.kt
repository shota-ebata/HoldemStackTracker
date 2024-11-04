package com.ebata_shota.holdemstacktracker.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

//    @Provides
//    @Singleton
//    fun provideFirebaseAnalytics(): FirebaseAnalytics {
//        return Firebase.analytics
//    }
}
