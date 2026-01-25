package com.ebata_shota.holdemstacktracker.di.module

import com.ebata_shota.holdemstacktracker.domain.usecase.AddBetPhaseActionInToGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.BanPlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteAllInUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteCallUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteCheckUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteFoldUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteRaiseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteTransitionToNextPhaseIfNeedUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteOwnAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionTypeInLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetActionablePlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetBetPhaseActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetCurrentPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetFirstActionPlayerIdOfNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMaxBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMinRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetMyPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextBtnPlayerIdUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameFromIntervalUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGamePlayerStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneDownRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetOneUpRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionInPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPlayerLastActionsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPotListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByPotSliderUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeByStackSliderUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetRaiseSizeUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.HasErrorChipSizeTextValueUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsActionRequiredInPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsCurrentPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsEnableCheckUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsNotRaisedYetUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.JoinPlayerFromWaitPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RequestJoinTableUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.MovePositionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RemovePlayersUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.RenameTablePlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.SetPotSettlementInfoUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.AddBetPhaseActionInToGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.BanPlayersUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.CreateNewGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteAllInUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteCallUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteCheckUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteFoldUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteRaiseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteTransitionToNextPhaseIfNeedUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.ExecuteOwnAutoActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetActionTypeInLastPhaseAsBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetActionablePlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetAddedAutoActionsGameUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetBetPhaseActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetCurrentPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetFirstActionPlayerIdOfNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetLastPhaseAsBetPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMaxBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMinRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetMyPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextAutoActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextBtnPlayerIdUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGameFromIntervalUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextGamePlayerStateListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNextPlayerStackUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetNotFoldPlayerIdsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetOneDownRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetOneUpRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetPerPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPendingBetSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionInPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPlayerLastActionsUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetPotListUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetRaiseSizeByPotSliderUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetRaiseSizeByStackSliderUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.GetRaiseSizeUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.HasErrorChipSizeTextValueUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsActionRequiredInPhaseUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsCurrentPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsEnableCheckUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.IsNotRaisedYetUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.JoinPlayerFromWaitPlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.RequestJoinTableUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.MovePositionUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.RemovePlayersUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.RenameTablePlayerUseCaseImpl
import com.ebata_shota.holdemstacktracker.domain.usecase.impl.SetPotSettlementInfoUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {
    @Binds
    fun bindGetLastPhaseAsBetPhaseUseCase(useCase: GetLastPhaseAsBetPhaseUseCaseImpl): GetLastPhaseAsBetPhaseUseCase

    @Binds
    fun bindGetMaxBetSizeUseCase(useCase: GetMaxBetSizeUseCaseImpl): GetMaxBetSizeUseCase

    @Binds
    fun bindGetNextPhaseUseCase(useCase: GetNextPhaseUseCaseImpl): GetNextPhaseUseCase

    @Binds
    fun bindGetNextGamePlayerStateListUseCase(useCase: GetNextGamePlayerStateListUseCaseImpl): GetNextGamePlayerStateListUseCase

    @Binds
    fun bindAddBetPhaseActionInToGameUseCase(useCase: AddBetPhaseActionInToGameUseCaseImpl): AddBetPhaseActionInToGameUseCase

    @Binds
    fun bindGetPendingBetPerPlayerUseCase(useCase: GetPendingBetPerPlayerUseCaseImpl): GetPendingBetPerPlayerUseCase

    @Binds
    fun bindGetPotStateListUseCase(useCase: GetPotListUseCaseImpl): GetPotListUseCase

    @Binds
    fun bindIsActionRequiredUseCase(useCase: IsActionRequiredInPhaseUseCaseImpl): IsActionRequiredInPhaseUseCase

    @Binds
    fun bindGetNextPlayerStackUseCase(useCase: GetNextPlayerStackUseCaseImpl): GetNextPlayerStackUseCase

    @Binds
    fun bindGetPlayerLastActionsUseCase(useCase: GetPlayerLastActionsUseCaseImpl): GetPlayerLastActionsUseCase

    @Binds
    fun bindGetCurrentPlayerIdUseCase(useCase: GetCurrentPlayerIdUseCaseImpl): GetCurrentPlayerIdUseCase

    @Binds
    fun bindJoinTableUseCase(useCase: RequestJoinTableUseCaseImpl): RequestJoinTableUseCase

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
    fun bindGetRaiseSizeByPotSlider(useCase: GetRaiseSizeByPotSliderUseCaseImpl): GetRaiseSizeByPotSliderUseCase

    @Binds
    fun bindGetRaiseSizeByStackSlider(useCase: GetRaiseSizeByStackSliderUseCaseImpl): GetRaiseSizeByStackSliderUseCase

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
    fun bindGetActionTypeInLastPhaseAsBetPhaseUseCase(useCase: GetActionTypeInLastPhaseAsBetPhaseUseCaseImpl): GetActionTypeInLastPhaseAsBetPhaseUseCase

    @Binds
    fun bindGetBetPhaseActionUseCase(useCase: GetBetPhaseActionUseCaseImpl): GetBetPhaseActionUseCase

    @Binds
    fun bindGetRequiredActionPlayerIdsUseCase(useCase: GetActionablePlayerIdsUseCaseImpl): GetActionablePlayerIdsUseCase

    @Binds
    fun bindGetFirstActionPlayerIdOfNextPhaseUseCase(useCase: GetFirstActionPlayerIdOfNextPhaseUseCaseImpl): GetFirstActionPlayerIdOfNextPhaseUseCase

    @Binds
    fun bindGetNextGameFromIntervalUseCase(useCase: GetNextGameFromIntervalUseCaseImpl): GetNextGameFromIntervalUseCase

    @Binds
    fun bindGetActivePlayerIdsUseCase(useCase: GetNotFoldPlayerIdsUseCaseImpl): GetNotFoldPlayerIdsUseCase

    @Binds
    fun bindSetPotSettlementInfoUseCase(useCase: SetPotSettlementInfoUseCaseImpl): SetPotSettlementInfoUseCase

    @Binds
    fun bindHasErrorChipSizeTextValueUseCase(useCase: HasErrorChipSizeTextValueUseCaseImpl): HasErrorChipSizeTextValueUseCase

    @Binds
    fun bindGetAddedAutoActionsGameUseCase(useCase: GetAddedAutoActionsGameUseCaseImpl): GetAddedAutoActionsGameUseCase

    @Binds
    fun bindIsEnableCheckUseCase(useCase: IsEnableCheckUseCaseImpl): IsEnableCheckUseCase

    @Binds
    fun bindIsCurrentPlayerUseCase(useCase: IsCurrentPlayerUseCaseImpl): IsCurrentPlayerUseCase

    @Binds
    fun bindExecuteFoldUseCase(useCase: ExecuteFoldUseCaseImpl): ExecuteFoldUseCase

    @Binds
    fun bindExecuteCheckUseCase(useCase: ExecuteCheckUseCaseImpl): ExecuteCheckUseCase

    @Binds
    fun bindExecuteAllInUseCase(useCase: ExecuteAllInUseCaseImpl): ExecuteAllInUseCase

    @Binds
    fun bindExecuteCallUseCase(useCase: ExecuteCallUseCaseImpl): ExecuteCallUseCase

    @Binds
    fun bindExecuteRaiseUseCase(useCase: ExecuteRaiseUseCaseImpl): ExecuteRaiseUseCase

    @Binds
    fun bindGetRaiseSizeUseCase(useCase: GetRaiseSizeUseCaseImpl): GetRaiseSizeUseCase

    @Binds
    fun bindExecuteProceedGameIfNeedUseCase(useCase: ExecuteTransitionToNextPhaseIfNeedUseCaseImpl): ExecuteTransitionToNextPhaseIfNeedUseCase

    @Binds
    fun bindGetNextBtnPlayerIdUseCase(useCase: GetNextBtnPlayerIdUseCaseImpl): GetNextBtnPlayerIdUseCase

    @Binds
    fun bindJoinPlayerFromWaitPlayer(useCase: JoinPlayerFromWaitPlayerUseCaseImpl): JoinPlayerFromWaitPlayerUseCase

    @Binds
    fun bindBanPlayersUseCase(useCase: BanPlayersUseCaseImpl): BanPlayersUseCase

    @Binds
    fun bindGetMyPlayerIdUseCase(useCase: GetMyPlayerIdUseCaseImpl): GetMyPlayerIdUseCase
    @Binds
    fun bindExecuteOwnAutoActionUseCase(useCase: ExecuteOwnAutoActionUseCaseImpl): ExecuteOwnAutoActionUseCase
}