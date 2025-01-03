package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.infra.db.AppDatabase
import com.ebata_shota.holdemstacktracker.infra.db.dao.ActionHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
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
    fun provideTableDao(appDatabase: AppDatabase): TableSummaryDao {
        return appDatabase.tableSummaryDao()
    }

    @Singleton
    @Provides
    fun provideActionDao(appDatabase: AppDatabase): ActionHistoryDao {
        return appDatabase.actionDao()
    }
}