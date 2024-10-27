package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.usecase.CurrentActionPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.CurrentActionPlayerIdUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {
    @Binds
    fun getCurrentActionPlayerIdUseCase(useCase: CurrentActionPlayerIdUseCaseImpl): CurrentActionPlayerIdUseCase
}