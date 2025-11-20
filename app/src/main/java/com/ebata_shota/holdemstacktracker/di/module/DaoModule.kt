package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.infra.db.AppDatabase
import com.ebata_shota.holdemstacktracker.infra.db.dao.ActionHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.dao.PhaseHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Singleton
    @Provides
    fun provideActionHistoryDao(appDatabase: AppDatabase): ActionHistoryDao {
        return appDatabase.actionHistoryDao()
    }

    @Singleton
    @Provides
    fun providePhaseHistoryDao(appDatabase: AppDatabase): PhaseHistoryDao {
        return appDatabase.phaseHistoryDao()
    }
}
