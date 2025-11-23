package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.DoTransitionToNextPhaseIfNeedUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetAddedAutoActionsGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetFirstActionPlayerIdOfNextPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGameFromIntervalUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

class DoTransitionToNextPhaseIfNeedUseCaseImpl
@Inject
constructor(
    private val getNextPlayerIdOfNextPhase: GetFirstActionPlayerIdOfNextPhaseUseCase,
    private val getNextGameFromInterval: GetNextGameFromIntervalUseCase,
    private val getAddedAutoActionsGame: GetAddedAutoActionsGameUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val gameRepository: GameRepository,
) : DoTransitionToNextPhaseIfNeedUseCase {
    override suspend fun invoke(
        game: Game,
        hostPlayerId: PlayerId,
        rule: Rule,
    ) {
        val myPlayerId = firebaseAuthRepository.myPlayerIdFlow.first()
        val nextPlayerId = getNextPlayerIdOfNextPhase.invoke(
            currentGame = game,
        )
        if (myPlayerId == nextPlayerId || myPlayerId == hostPlayerId) {
            // 次のプレイヤーだった場合
            // ホストプレイヤーの場合
            // （ホストの操作はフェーズを進める力を持たせる、特に精算のときはホストも操作できたほうが都合が良い）
            val nextGame = getNextGameFromInterval.invoke(
                currentGame = game
            )
            val addedAutoActionGame = getAddedAutoActionsGame.invoke(
                game = nextGame,
                rule = rule,
            )
            // ダイアログを消してから、実際に消した扱いにするまで
            // delayをかける
            delay(1000L)
            gameRepository.sendGame(
                tableId = game.tableId,
                newGame = addedAutoActionGame.copy(
                    updateTime = Instant.now()
                ),
            )
        }
    }
}