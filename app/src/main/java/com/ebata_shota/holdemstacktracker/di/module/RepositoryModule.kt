package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.DefaultRuleStateOfRingRepository
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GmsBarcodeScannerRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.QrBitmapRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RemoteConfigRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.infra.repository.ActionHistoryRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.DefaultRuleStateOfRingRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.FirebaseAuthRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.GameRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.GmsBarcodeScannerRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.PhaseHistoryRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.PrefRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.QrBitmapRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.RandomIdRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.RemoteConfigRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.TableRepositoryImpl
import com.ebata_shota.holdemstacktracker.infra.repository.TableSummaryRepositoryImpl
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
    fun bindTableStateRepository(repo: TableRepositoryImpl): TableRepository

    @Singleton
    @Binds
    fun bindTableSummaryRepository(repo: TableSummaryRepositoryImpl): TableSummaryRepository

    @Singleton
    @Binds
    fun bindGameStateRepository(repo: GameRepositoryImpl): GameRepository

    @Singleton
    @Binds
    fun bindRandomIdRepository(repo: RandomIdRepositoryImpl): RandomIdRepository

    @Singleton
    @Binds
    fun bindFirebaseAuthRepository(repo: FirebaseAuthRepositoryImpl): FirebaseAuthRepository

    @Singleton
    @Binds
    fun bindDefaultRingGameStateRepository(repo: DefaultRuleStateOfRingRepositoryImpl): DefaultRuleStateOfRingRepository

    @Singleton
    @Binds
    fun bindQrBitmapRepository(repo: QrBitmapRepositoryImpl): QrBitmapRepository

    @Singleton
    @Binds
    fun bindGmsBarcodeScannerRepository(repo: GmsBarcodeScannerRepositoryImpl): GmsBarcodeScannerRepository

    @Singleton
    @Binds
    fun bindActionHistoryRepository(repo: ActionHistoryRepositoryImpl): ActionHistoryRepository

    @Singleton
    @Binds
    fun bindPhaseHistoryRepository(repo: PhaseHistoryRepositoryImpl): PhaseHistoryRepository

    @Singleton
    @Binds
    fun bindRemoteConfigRepository(repo: RemoteConfigRepositoryImpl): RemoteConfigRepository
}
