package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastBetPhaseActionTypeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGamePlayerStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionInPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByPotSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSlider
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RemovePlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.CreateNewGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastBetPhaseLastActionTypeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetCurrentPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLatestBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMinRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextAutoActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetOneDownRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetOneUpRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionInPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPotStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetRaiseSizeByPotSliderImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetRaiseSizeByStackSliderImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsNotRaisedYetUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.JoinTableUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.MovePositionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.RemovePlayersUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.RenameTablePlayerUseCaseImpl
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
    fun bindGetNextGamePlayerStateListUseCase(useCase: GetNextGamePlayerStateListUseCaseImpl): GetNextGamePlayerStateListUseCase

    @Binds
    fun bindGetNextTableStateUseCase(useCase: GetNextGameUseCaseImpl): GetNextGameUseCase

    @Binds
    fun bindGetPendingBetPerPlayerUseCase(useCase: GetPendingBetPerPlayerUseCaseImpl): GetPendingBetPerPlayerUseCase

    @Binds
    fun bindGetPotStateListUseCase(useCase: GetPotStateListUseCaseImpl): GetPotStateListUseCase

    @Binds
    fun bindIsActionRequiredUseCase(useCase: IsActionRequiredUseCaseImpl): IsActionRequiredUseCase

    @Binds
    fun bindGetNextPlayerStackUseCase(useCase: GetNextPlayerStackUseCaseImpl): GetNextPlayerStackUseCase

    @Binds
    fun bindGetPlayerLastActionsUseCase(useCase: GetPlayerLastActionsUseCaseImpl): GetPlayerLastActionsUseCase

    @Binds
    fun bindGetCurrentPlayerIdUseCase(useCase: GetCurrentPlayerIdUseCaseImpl): GetCurrentPlayerIdUseCase

    @Binds
    fun bindJoinTableUseCase(useCase: JoinTableUseCaseImpl): JoinTableUseCase

    @Binds
    fun bindCreateNewGameUseCase(useCase: CreateNewGameUseCaseImpl): CreateNewGameUseCase

    @Binds
    fun bindMovePositionUseCase(useCase: MovePositionUseCaseImpl): MovePositionUseCase

    @Binds
    fun bindRemovePlayersUseCase(useCase: RemovePlayersUseCaseImpl): RemovePlayersUseCase

    @Binds
    fun bindRenameTableBasePlayerUseCase(useCase: RenameTablePlayerUseCaseImpl): RenameTablePlayerUseCase

    @Binds
    fun bindGetNextAutoActionUseCase(useCase: GetNextAutoActionUseCaseImpl): GetNextAutoActionUseCase

    @Binds
    fun bindGetCallSizeUseCase(useCase: GetMinRaiseSizeUseCaseImpl): GetMinRaiseSizeUseCase

    @Binds
    fun bindIsNotRaisedYetUseCase(useCase: IsNotRaisedYetUseCaseImpl): IsNotRaisedYetUseCase

    @Binds
    fun bindGetRaiseSizeByPotSlider(useCase: GetRaiseSizeByPotSliderImpl): GetRaiseSizeByPotSlider

    @Binds
    fun bindGetRaiseSizeByStackSlider(useCase: GetRaiseSizeByStackSliderImpl): GetRaiseSizeByStackSlider

    @Binds
    fun bindGetPendingBetSize(useCase: GetPendingBetSizeUseCaseImpl): GetPendingBetSizeUseCase

    @Binds
    fun bindGetOneDownRaiseSizeUseCase(useCase: GetOneDownRaiseSizeUseCaseImpl): GetOneDownRaiseSizeUseCase

    @Binds
    fun bindGetOneUpRaiseSizeUseCase(useCase: GetOneUpRaiseSizeUseCaseImpl): GetOneUpRaiseSizeUseCase

    @Binds
    fun bindGetPlayerLastActionUseCase(useCase: GetPlayerLastActionUseCaseImpl): GetPlayerLastActionUseCase

    @Binds
    fun bindGetPlayerLastActionInPhaseUseCase(useCase: GetPlayerLastActionInPhaseUseCaseImpl): GetPlayerLastActionInPhaseUseCase

    @Binds
    fun bindGetLastBetPhaseActionTypeUseCase(useCase: GetLastBetPhaseLastActionTypeUseCaseImpl): GetLastBetPhaseActionTypeUseCase
}