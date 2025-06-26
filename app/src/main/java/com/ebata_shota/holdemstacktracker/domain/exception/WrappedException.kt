package com.ebata_shota.holdemstacktracker.domain.exception

/**
 * Exceptionをラップするためのクラス
 *
 */
class WrappedException(
    message: String,
    cause: Exception
) : Exception(message, cause) {

    /**
     * 例外の根本的な原因を取得するプロパティ
     *
     * このプロパティは、WrappedExceptionがラップしている例外の根本的な原因を返します。
     * もしWrappedExceptionがラップしている例外が別のWrappedExceptionであれば、その根本的な原因を再帰的に取得します。
     */
    val rootCause: Throwable?
        get() = cause?.let {
            if (it is WrappedException) {
                it.rootCause
            } else {
                it
            }
        }
}