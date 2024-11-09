package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.repository.PrefRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.GameStateRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.TableStateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindTableStateRepository(repo: TableStateRepositoryImpl): TableStateRepository

    @Binds
    fun bindPrefRepository(repo: PrefRepositoryImpl): PrefRepository

    @Binds
    fun bindGameStateRepository(repo: GameStateRepositoryImpl): GameStateRepository
}