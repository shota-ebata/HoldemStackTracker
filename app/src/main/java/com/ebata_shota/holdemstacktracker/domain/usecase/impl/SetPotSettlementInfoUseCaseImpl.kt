package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.GameResult
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PotSettlementInfo
import com.ebata_shota.holdemstacktracker.domain.repository.GameRepository
import com.ebata_shota.holdemstacktracker.domain.repository.RandomIdRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNotFoldPlayerIdsUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.SetPotSettlementInfoUseCase
import com.ebata_shota.holdemstacktracker.domain.util.getSortedByActionOrder
import com.ebata_shota.holdemstacktracker.ui.compose.dialog.PotSettlementDialogUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Pot精算処理
 */
class SetPotSettlementInfoUseCaseImpl
@Inject
constructor(
    private val gameRepository: GameRepository,
    private val randomIdRepository: RandomIdRepository,
    private val notFoldPlayerIds: GetNotFoldPlayerIdsUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher,
) : SetPotSettlementInfoUseCase {

    override suspend fun invoke(
        game: Game,
        pots: List<PotSettlementDialogUiState.PotUiState>,
    ) = withContext(dispatcher) {
        // アクション順にソートされたプレイヤーリストを取得
        val playerOrder = game.playerOrder
        val actionOrder = playerOrder.getSortedByActionOrder(btnPlayerId = game.btnPlayerId)
        // フォールドしていないプレイヤーIDのリストを取得
        val notFoldPlayerIds = notFoldPlayerIds.invoke(playerOrder = playerOrder, game.phaseList)
        // SBに一番近いプレイヤー（アクションが一番早いプレイヤー）を取得
        val nearestSbPlayerId = actionOrder.first { playerId ->
            notFoldPlayerIds.any { it == playerId }
        }
        // Potの精算情報を作成
        val potSettlementInfoList: List<PotSettlementInfo> = game.potList.map { pot ->
            val potUiState = pots.find { it.potId == pot.id }!!
            // そのPotを獲得したプレイヤーのIDリスト
            val selectedPlayerIds: List<PlayerId> = potUiState.players
                .filter { it.isSelected }
                .map { it.playerId }
            PotSettlementInfo(
                potId = pot.id,
                potNumber = pot.potNumber,
                potSize = pot.potSize,
                acquirerPlayerIds = selectedPlayerIds,
            )
        }

        // Pot獲得後の各プレイヤーの状態を計算
        val gamePlayers = getNewGamePlayers(
            players = game.players,
            nearestSbPlayerId = nearestSbPlayerId,
            potSettlementInfoList = potSettlementInfoList,
        )

        // ゲーム終了のフェーズを作成
        val nextPhase = getEndPhase(potSettlementInfoList)

        // Repositoryにゲーム情報を送信
        gameRepository.sendGame(
            tableId = game.tableId,
            newGame = game.copy(
                players = gamePlayers,
                phaseList = game.phaseList + nextPhase,
                potList = emptyList() // Potをリセット
            ),
        )
    }

    /**
     * ゲーム終了フェーズを作成する
     * @param potSettlementInfoList Potの精算情報
     * @return [Phase.End]
     */
    private fun getEndPhase(
        potSettlementInfoList: List<PotSettlementInfo>
    ): Phase.End {
        return Phase.End(
            phaseId = PhaseId(randomIdRepository.generateRandomId()),
            gameResult = GameResult(
                potResults = potSettlementInfoList
                    .mapIndexed { index, potSettlementInfo ->
                        GameResult.PotResult(
                            id = potSettlementInfo.potId,
                            potNumber = potSettlementInfo.potNumber,
                            potSize = potSettlementInfo.potSize,
                            winnerPlayerIds = potSettlementInfo.acquirerPlayerIds
                        )
                    }
            ),
        )
    }

    /**
     * Pot獲得後の各プレイヤーの状態を計算する
     * @param players 現在のプレイヤーリスト
     * @param potSettlementInfoList Potの精算情報
     * @param nearestSbPlayerId SBに一番近いプレイヤーのID
     * @return [List<GamePlayer>]
     */
    private fun getNewGamePlayers(
        players: List<GamePlayer>,
        potSettlementInfoList: List<PotSettlementInfo>,
        nearestSbPlayerId: PlayerId,
    ): List<GamePlayer> {
        var gamePlayers = players.toList()
        potSettlementInfoList.forEach { potSettlementInfo ->
            val potSize = potSettlementInfo.potSize
            val acquirerPlayerSize = potSettlementInfo.acquirerPlayerIds.size
            // Potを山分けした際の余り
            val mod: Int = potSize % acquirerPlayerSize
            // Potを山分けした際の各プレイヤーの獲得額
            val acquireSize = (potSize - mod) / acquirerPlayerSize
            gamePlayers = gamePlayers.map { gamePlayer ->
                if (potSettlementInfo.acquirerPlayerIds.any { it == gamePlayer.id }) {
                    var nextStack = gamePlayer.stack + acquireSize
                    if (gamePlayer.id == nearestSbPlayerId) {
                        // 「あまり」をSBに近い人に付与
                        nextStack += mod
                    }
                    gamePlayer.copy(
                        stack = nextStack
                    )
                } else {
                    gamePlayer
                }
            }
        }
        return gamePlayers
    }
}
