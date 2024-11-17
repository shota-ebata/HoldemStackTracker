package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRingGameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameStateRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.repository.DefaultRingGameStateRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.FirebaseAuthRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.GameStateRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.PrefRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.RandomIdRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.TableStateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindPrefRepository(repo: PrefRepositoryImpl): PrefRepository

    @Singleton
    @Binds
    fun bindTableStateRepository(repo: TableStateRepositoryImpl): TableStateRepository

    @Singleton
    @Binds
    fun bindGameStateRepository(repo: GameStateRepositoryImpl): GameStateRepository

    @Singleton
    @Binds
    fun bindRandomIdRepository(repo: RandomIdRepositoryImpl): RandomIdRepository

    @Singleton
    @Binds
    fun bindFirebaseAuthRepository(repo: FirebaseAuthRepositoryImpl): FirebaseAuthRepository

    @Singleton
    @Binds
    fun bindDefaultRingGameStateRepository(repo: DefaultRingGameStateRepositoryImpl): DefaultRingGameStateRepository
}