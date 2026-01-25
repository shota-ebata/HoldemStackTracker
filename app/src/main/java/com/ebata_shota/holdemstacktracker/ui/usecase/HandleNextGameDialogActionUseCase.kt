package com.ebata_shota.holdemstacktracker.ui.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import com.ebata_shota.holdemstacktracker.domain.repository.TableRepository
import com.ebata_shota.holdemstacktracker.domain.usecase.CreateNewGameUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextBtnPlayerIdUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class HandleNextGameDialogActionUseCase
@Inject
constructor(
    private val tableRepository: TableRepository,
    private val getNextBtnPlayerId: GetNextBtnPlayerIdUseCase,
    private val createNewGame: CreateNewGameUseCase,
) {
    // TODO: ゲームを継続できる条件はもっと厳しいかもしれないので問題ないか確認したい
    //  （構成メンバー変わってたら次のゲーム行かないほうがいいかも？）
    suspend fun invoke(
        table: Table,
        game: Game,
        hideNextGameDialog: () -> Unit,
        navigateToTablePrepare: suspend () -> Unit,
    ) {
        if (
            table.playerOrderWithoutLeaved.size in 2..10
            && table.basePlayers.none { it.stack < table.rule.minBetSize }
        ) {
            // 次のゲームに行けそうなら行く
            // ・参加プレイヤー人数
            // ・参加者のスタック
            val nextBtnPlayerId = getNextBtnPlayerId(table, game)
            if (nextBtnPlayerId != null) {
                createNewGame(table.copy(btnPlayerId = nextBtnPlayerId))
                tableRepository.updateTableStatus(
                    tableId = table.id,
                    tableStatus = TableStatus.PREPARING,
                )
            } else {
                // TODO: ゲーム開始できない旨のトーストを表示する
                // BTNが取得できないなら、準備画面に戻る
                tableRepository.updateTableStatus(
                    tableId = table.id,
                    tableStatus = TableStatus.PREPARING,
                )
                navigateToTablePrepare()
            }
            hideNextGameDialog()
        } else {
            // TODO: ゲーム開始できない旨のトーストを表示する
            // 次のゲームに行けないなら、準備画面に戻る
            tableRepository.updateTableStatus(
                tableId = table.id,
                tableStatus = TableStatus.PREPARING,
            )
            navigateToTablePrepare()
        }
    }
}