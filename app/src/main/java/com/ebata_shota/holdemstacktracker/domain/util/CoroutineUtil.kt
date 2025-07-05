package com.ebata_shota.holdemstacktracker.domain.util

import com.ebata_shota.holdemstacktracker.domain.exception.WrappedException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * withContextを使用して、例外をラップするための関数
 *
 * @param context CoroutineContext
 * @param block suspend CoroutineScope.() -> T
 * @return T
 *
 * 呼び出し元のメソッド名と行番号を取得し、例外が発生した場合にその情報を含む[WrappedException]を投げる。
 *
 *
 */
suspend fun <T> withContextAndExceptionWrapper(
    context: CoroutineContext,
    coroutineName: String,
    block: suspend CoroutineScope.() -> T,
): T {
    // 呼び出し元のメソッド名と行番号を取得
    // ただし、難読化されている場合は、ファイル名や行番号を取得できない可能性がある。ので不採用
//    val callerStackTraceElement = Throwable().stackTrace[1] // 呼び出しているメソッド
//    val callerOfCallerStackTraceElement = Throwable().stackTrace[2] // 呼び出しているメソッドの更に呼び出し元
//    var callerInfo =
//        "(${callerOfCallerStackTraceElement.fileName}:${callerOfCallerStackTraceElement.lineNumber}→${callerStackTraceElement.fileName}:${callerStackTraceElement.lineNumber})"
    return withContext(CoroutineName(coroutineName)) {
        try {
            withContext(context) {
                block()
            }
        } catch (e: CancellationException) {
            // CancellationExceptionはそのまま投げる(suspend中断例外の伝播のため)
            throw e
        } catch (e: Exception) {
            val callerInfo = coroutineContext[CoroutineName]?.name!!
            throw WrappedException(callerInfo, e)
        }
    }
}

suspend fun <T> withContext(
    coroutineName: String,
    block: suspend CoroutineScope.() -> T,
): T {
    return withContext(
        context = CoroutineName(name = coroutineName),
        block = block
    )
}
