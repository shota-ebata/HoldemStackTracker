package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameStateUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPodStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextTableStateUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPodStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {
    @Binds
    fun bindGetLatestBetPhaseUseCase(useCase: GetLatestBetPhaseUseCaseImpl): GetLatestBetPhaseUseCase

    @Binds
    fun bindGetMaxBetSizeUseCase(useCase: GetMaxBetSizeUseCaseImpl): GetMaxBetSizeUseCase

    @Binds
    fun bindGetNextPhaseUseCase(useCase: GetNextPhaseUseCaseImpl): GetNextPhaseUseCase

    @Binds
    fun bindGetNextPlayerStateListUseCase(useCase: GetNextPlayerStateListUseCaseImpl): GetNextPlayerStateListUseCase

    @Binds
    fun bindGetNextTableStateUseCase(useCase: GetNextTableStateUseCaseImpl): GetNextGameStateUseCase

    @Binds
    fun bindGetPendingBetPerPlayerUseCase(useCase: GetPendingBetPerPlayerUseCaseImpl): GetPendingBetPerPlayerUseCase

    @Binds
    fun bindGetPodStateListUseCase(useCase: GetPodStateListUseCaseImpl): GetPodStateListUseCase

    @Binds
    fun bindIsActionRequiredUseCase(useCase: IsActionRequiredUseCaseImpl): IsActionRequiredUseCase

    @Binds
    fun bindGetNextPlayerStackUseCase(useCase: GetNextPlayerStackUseCaseImpl): GetNextPlayerStackUseCase

    @Binds
    fun bindGetPlayerLastActionsUseCase(useCase: GetPlayerLastActionsUseCaseImpl): GetPlayerLastActionsUseCase
}