package com.ebata_shota.holdemstacktracker.di.module


import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class CoroutineDispatcherModule {

    @CoroutineDispatcherIO
    @Provides
    fun provideDispatchersIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @CoroutineDispatcherDefault
    @Provides
    fun provideDispatchersDefault(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    @CoroutineDispatcherMain
    @Provides
    fun provideDispatchersMain(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}
