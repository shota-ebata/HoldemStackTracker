package com.ebata_shota.holdemstacktracker.domain.util

import com.ebata_shota.holdemstacktracker.domain.extension.rearrangeListFromIndex
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

/**
 * 基本的なアクション順にソートする
 * 2人でのPreFlopだけは、このメソッドでは順番を取得できない
 */
fun List<PlayerId>.getSortedByActionOrder(
    btnPlayerId: PlayerId,
): List<PlayerId> {
    val btnIndex = this.indexOf(btnPlayerId)
    val startIndex = (btnIndex + 1) % this.size
    return this.rearrangeListFromIndex(startIndex = startIndex)
}